package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;  // Ignore the message if it's from a bot
        }

        String message = event.getMessage().getContentRaw();
        if (message.startsWith("!TIMER")) {
            timerStuff(event);
        }
    }

    public void timerStuff(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length < 2) {
            event.getChannel().sendMessage("❌ Please specify the number of seconds.").queue();
            return;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(args[1]);
            if (seconds <= 0) {
                event.getChannel().sendMessage("❌ Time must be greater than 0.").queue();
                return;
            }
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("❌ Invalid number.").queue();
            return;
        }

        event.getChannel().sendMessage("⏳ Timer: " + seconds + "s").queue(timerMessage -> {
            final int[] timeLeft = {seconds};

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(() -> {
                if (timeLeft[0] <= 0) {
                    scheduler.shutdown();
                    timerMessage.reply(event.getAuthor().getAsMention() + " YOUR TIME HAS COME TO AN END :index_pointing_at_the_viewer::robot:").queue();
                } else {
                    timerMessage.editMessage("⏳ Timer: " + timeLeft[0] + "s").queue();
                    timeLeft[0]--;
                }
            }, 0, 1, TimeUnit.SECONDS);
        });
    }
}
