package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HandleReminderCommand {

    public static CommandData getCommandData() {
        return Commands.slash("remind", "Set a reminder for yourself or another user")
                .addOption(OptionType.USER, "user", "The user to remind", true)
                .addOption(OptionType.STRING, "time", "When to remind (e.g., 5m, 10s, 1h)", true)
                .addOption(OptionType.STRING, "message", "The reminder message", true);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        User user = event.getOption("user").getAsUser();
        String timeInput = event.getOption("time").getAsString();
        String reminderMessage = event.getOption("message").getAsString();

        User reminderUser = event.getUser();

        int delaySeconds = parseTimeToSeconds(timeInput);
        if (delaySeconds <= 0) {
            event.reply("❌ Invalid time format. Use something like `10s`, `5m`, or `1h`.").setEphemeral(true).queue();
            return;
        }

        event.reply("⏰ Reminder set for " + user.getName() + " in " + timeInput + ": " + reminderMessage).queue();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            event.getChannel().sendMessage(user.getAsMention() + " 🔔 Reminder: " + reminderMessage).queue(reminderMsg -> {
                // Send an ephemeral follow-up (does NOT reply visually, but confirms)
                event.getHook().sendMessage("Reminder sent by " + reminderUser.getAsMention())
                        .setEphemeral(true)
                        .queue();
            });
            scheduler.shutdown();
        }, delaySeconds, TimeUnit.SECONDS);
    }

    private static int parseTimeToSeconds(String input) {
        input = input.toLowerCase().trim();
        try {
            if (input.endsWith("s")) {
                return Integer.parseInt(input.substring(0, input.length() - 1));
            } else if (input.endsWith("m")) {
                return Integer.parseInt(input.substring(0, input.length() - 1)) * 60;
            } else if (input.endsWith("h")) {
                return Integer.parseInt(input.substring(0, input.length() - 1)) * 3600;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }
}