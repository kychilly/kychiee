package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
                        new OptionData(
                                OptionType.STRING,
                                "time",
                                "Time duration (e.g. 10s, 5m, 2h)",
                                true
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "user",
                                "User ID or mention to notify",
                                false
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "message",
                                "Message for the timer",
                                false
                        )
                );
    }

    public static void execute(SlashCommandInteractionEvent event) {

        String timeInput = event.getOption("time").getAsString();
        int seconds = parseTimeToSeconds(timeInput);

        if (seconds <= 0) {
            event.reply(
                    "❌ Invalid time format. Use formats like `10s`, `5m`, `2h`."
            ).setEphemeral(true).queue();
            return;
        }

        // Optional message
        String timerMessage = event.getOption("message") != null
                ? event.getOption("message").getAsString()
                : "Your timer has finished!";

        // If no user was specified, use the person who ran the command
        if (event.getOption("user") == null) {

            startTimer(
                    event,
                    event.getUser(),
                    seconds,
                    timerMessage
            );

            return;
        }

        // Get the supplied user value
        String userInput = event.getOption("user").getAsString().trim();

        // Convert <@123456789> or <@!123456789> into 123456789
        String userId = extractUserId(userInput);

        if (userId == null) {
            event.reply(
                    "❌ Invalid user. Please provide a Discord user ID or mention."
            ).setEphemeral(true).queue();
            return;
        }

        // Retrieve the user globally
        event.getJDA().retrieveUserById(userId).queue(
                targetUser -> startTimer(
                        event,
                        targetUser,
                        seconds,
                        timerMessage
                ),
                error -> event.reply(
                        "❌ I couldn't find a Discord user with that ID."
                ).setEphemeral(true).queue()
        );
    }

    private static String extractUserId(String input) {

        // Normal Discord mention:
        // <@123456789>
        if (input.matches("<@\\d+>")) {
            return input.substring(2, input.length() - 1);
        }

        // Older Discord mention format:
        // <@!123456789>
        if (input.matches("<@!\\d+>")) {
            return input.substring(3, input.length() - 1);
        }

        // Raw Discord ID
        if (input.matches("\\d+")) {
            return input;
        }

        return null;
    }

    private static void startTimer(
            SlashCommandInteractionEvent event,
            User targetUser,
            int seconds,
            String timerMessage
    ) {

        targetUser.openPrivateChannel().queue(
                privateChannel -> {

                    final int[] timeLeft = {seconds};
                    int initialTime = seconds;

                    EmbedBuilder timerEmbed = new EmbedBuilder()
                            .setTitle("SUPER COOL TIMER ACTIVATED!!!")
                            .setThumbnail(
                                    "https://media.discordapp.net/attachments/1186115783013711894/1419106737989877931/Z.png"
                            )
                            .setFooter("Your dont come")
                            .setDescription(
                                    "⏳ **Time remaining:** " +
                                            formatTime(timeLeft[0]) +
                                            " / " +
                                            formatTime(initialTime) +
                                            "\n\n" +
                                            "📝 **Message:** " +
                                            timerMessage
                            );

                    // Send timer to user's DMs
                    privateChannel.sendMessageEmbeds(
                            timerEmbed.build()
                    ).queue(dmMessage -> {

                        ScheduledExecutorService scheduler =
                                Executors.newSingleThreadScheduledExecutor();

                        scheduler.scheduleAtFixedRate(() -> {

                            if (timeLeft[0] < 0) {

                                scheduler.shutdown();

                                EmbedBuilder finishedEmbed =
                                        new EmbedBuilder()
                                                .setTitle(
                                                        "SUPER COOL TIMER ACTIVATED!!!"
                                                )
                                                .setThumbnail(
                                                        "https://media.discordapp.net/attachments/1186115783013711894/1419106737989877931/Z.png"
                                                )
                                                .setFooter("Your dont come")
                                                .setDescription(
                                                        "⏰ **TIMER FINISHED!**\n\n" +
                                                                "📝 **Message:** " +
                                                                timerMessage
                                                );

                                dmMessage.editMessageEmbeds(
                                        finishedEmbed.build()
                                ).queue();

                            } else {

                                EmbedBuilder countdownEmbed =
                                        new EmbedBuilder()
                                                .setTitle(
                                                        "SUPER COOL TIMER ACTIVATED!!!"
                                                )
                                                .setThumbnail(
                                                        "https://media.discordapp.net/attachments/1186115783013711894/1419106737989877931/Z.png"
                                                )
                                                .setFooter("Your dont come")
                                                .setDescription(
                                                        "⏳ **Time remaining:** " +
                                                                formatTime(timeLeft[0]) +
                                                                " / " +
                                                                formatTime(initialTime) +
                                                                "\n\n" +
                                                                "📝 **Message:** " +
                                                                timerMessage
                                                );

                                dmMessage.editMessageEmbeds(
                                        countdownEmbed.build()
                                ).queue();

                                timeLeft[0]--;
                            }

                        }, 0, 1, TimeUnit.SECONDS);
                    });

                    // Confirm in the server
                    event.reply(
                            "⏱️ Timer started for **" +
                                    targetUser.getName() +
                                    "**. Check their DMs!"
                    ).setEphemeral(true).queue();

                },
                error -> event.reply(
                        "❌ I couldn't send a DM to **" +
                                targetUser.getName() +
                                "**. They may have DMs disabled."
                ).setEphemeral(true).queue()
        );
    }

    private static int parseTimeToSeconds(String input) {

        input = input.toLowerCase().trim();

        try {

            if (input.endsWith("s")) {

                return Integer.parseInt(
                        input.substring(0, input.length() - 1)
                );

            } else if (input.endsWith("m")) {

                return Integer.parseInt(
                        input.substring(0, input.length() - 1)
                ) * 60;

            } else if (input.endsWith("h")) {

                return Integer.parseInt(
                        input.substring(0, input.length() - 1)
                ) * 3600;

            } else if (input.matches("\\d+")) {

                // Numbers without a unit = seconds
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

            return String.format(
                    "%dh %dm %ds",
                    hours,
                    minutes,
                    secs
            );

        } else if (minutes > 0) {

            return String.format(
                    "%dm %ds",
                    minutes,
                    secs
            );

        } else {

            return String.format(
                    "%ds",
                    secs
            );
        }
    }
}