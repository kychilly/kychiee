package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerCommand {

    public static CommandData getCommandData() {
        return Commands.slash("timer", "Set a timer that counts down")
                .addOptions(
                        new OptionData(OptionType.STRING, "time", "Time duration (e.g. 10s, 5m, 2h)", true),
                        new OptionData(OptionType.USER, "user", "User to notify when timer ends", false)
                );
    }

    public static void execute(SlashCommandInteractionEvent event) {
        String timeInput = event.getOption("time").getAsString();
        int seconds = parseTimeToSeconds(timeInput);

        if (seconds <= 0) {
            event.reply("❌ Invalid time format. Use formats like 10s, 5m, 2h").setEphemeral(true).queue();
            return;
        }

        String userMention = event.getOption("user") != null
                ? event.getOption("user").getAsUser().getAsMention()
                : event.getUser().getAsMention();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("SUPER COOL TIMER ACTIVATED!!!")
                .setThumbnail("https://media.discordapp.net/attachments/1186115783013711894/1419106737989877931/Z.png?ex=68d08da4&is=68cf3c24&hm=d1239e82ac023122d5c0f3fadbe4bc45d479424f716fba6bd3b7cd0c46abe903&=&format=webp&quality=lossless&width=126&height=224")
                .setFooter("Your dont come")
                .setDescription("⏳ Timer set for " + formatTime(seconds) + "\nTarget: " + userMention);


        event.replyEmbeds(embed.build()).queue(replyEmbed -> {
            final int[] timeLeft = {seconds};
            int initialTime = timeLeft[0];

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            EmbedBuilder noobEmbed = new EmbedBuilder()
                    .setTitle("SUPER COOL TIMER ACTIVATED!!!")
                    .setFooter("Your dont come")
                    .setThumbnail("https://media.discordapp.net/attachments/1186115783013711894/1419106737989877931/Z.png?ex=68d08da4&is=68cf3c24&hm=d1239e82ac023122d5c0f3fadbe4bc45d479424f716fba6bd3b7cd0c46abe903&=&format=webp&quality=lossless&width=126&height=224");

            scheduler.scheduleAtFixedRate(() -> {
                if (timeLeft[0] < 0) { // To make the time actually say 0s before pinging
                    scheduler.shutdown();
                    event.getHook().sendMessage(userMention + " YOUR TIME HAS COME TO AN END :index_pointing_at_the_viewer::robot:").queue();
                    //replyEmbed.editOriginal(userMention + " YOUR TIME HAS COME TO AN END :index_pointing_at_the_viewer::robot:").queue();
                } else {
                    noobEmbed.setDescription("⏳ Time remaining: " + formatTime(timeLeft[0]) + " / " + formatTime(initialTime) + "\nTarget: " + userMention);
                    replyEmbed.editOriginalEmbeds(noobEmbed.build()).queue();
                    timeLeft[0]--;
                }
            }, 0, 1, TimeUnit.SECONDS);
        });
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
            } else if (input.matches("\\d+")) { // Just numbers, default to seconds
                return Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }

    private static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
}