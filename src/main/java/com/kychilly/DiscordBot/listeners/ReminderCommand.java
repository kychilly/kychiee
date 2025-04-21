package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderCommand extends ListenerAdapter {


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;  // Ignore the message if it's from a bot
        }

        String message = event.getMessage().getContentRaw(); // Works here

        if (message.startsWith("!remind")) {
            String[] args = message.split(" ", 4);

            if (args.length < 4) {
                event.getChannel().sendMessage("❌ Usage: `!remind @User 5m Your reminder message`").queue();
                return;
            }

            String ping = args[1];           // @mention (like <@123456789>)
            String timeInput = args[2];      // 5m, 10s, etc.
            String reminderText = args[3];   // Everything after

            remindUser(event, ping, timeInput, reminderText);
        }



    }

    public void remindUser(MessageReceivedEvent event, String ping, String timeInput, String reminderMessage) {
        // Validate mention
        if (!ping.startsWith("<@") || !ping.endsWith(">")) {
            event.getChannel().sendMessage("❌ Invalid mention format. Use `@User`.").queue();
            return;
        }

        int delaySeconds = parseTimeToSeconds(timeInput);
        if (delaySeconds <= 0) {
            event.getChannel().sendMessage("❌ Invalid time format. Use something like `10s`, `5m`, or `1h`.").queue();
            return;
        }

        event.getChannel().sendMessage("⏰ Reminder set for " + ping + " in " + timeInput + ".").queue();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            event.getChannel().sendMessage(ping + " 🔔 Reminder: " + reminderMessage).queue();
            scheduler.shutdown();
        }, delaySeconds, TimeUnit.SECONDS);
    }


    private int parseTimeToSeconds(String input) {
        input = input.toLowerCase().trim();
        try {
            if (input.endsWith("s")) {
                return Integer.parseInt(input.replace("s", ""));
            } else if (input.endsWith("m")) {
                return Integer.parseInt(input.replace("m", "")) * 60;
            } else if (input.endsWith("h")) {
                return Integer.parseInt(input.replace("h", "")) * 3600;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }


}
