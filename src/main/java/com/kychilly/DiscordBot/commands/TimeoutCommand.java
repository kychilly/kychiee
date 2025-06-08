package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TimeoutCommand {

    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d+)([smhd])$");

    public static CommandData getCommandData() {
        return Commands.slash("timeout", "Timeouts a user")
                .addOption(OptionType.USER, "user", "The user to timeout", true)
                .addOption(OptionType.STRING, "time", "Duration of timeout (e.g. 30s, 15m, 2h, 1d)", true)
                .addOption(OptionType.STRING, "reason", "The reason for timing out", false)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
    }

    public static void handleCommand(SlashCommandInteractionEvent event) {
        // Check permissions

        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("You don't have permission to timeout members!").setEphemeral(true).queue();
            return;
        }

// Get command options
        User user = event.getOption("user").getAsUser();
        String timeInput = event.getOption("time").getAsString().toLowerCase();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        try {
            // Parse the duration
            Duration duration = parseDuration(timeInput);
            Instant timeoutUntil = Instant.now().plus(duration);

            // Validate duration doesn't exceed Discord's 28-day limit
            if (duration.toDays() > 28) {
                event.reply("❌ Maximum timeout duration is 28 days").setEphemeral(true).queue();
                return;
            }

            // Get the member and apply timeout
            event.getGuild().retrieveMember(user).queue(member -> {
                // First apply the timeout
                member.timeoutUntil(timeoutUntil).reason(reason).queue(
                        success -> {
                            // Public response in the server
                            String publicResponse = "⏳ " + user.getAsMention() + " has been timed out for "
                                    + formatDuration(duration) + ".\n"
                                    + "Reason: " + reason;
                            event.reply(publicResponse).queue();

                            // Try to send DM to the user
                            user.openPrivateChannel().queue(
                                    privateChannel -> {
                                        String dmMessage = "⚠️ **Timeout Notice** ⚠️\n\n"
                                                + "You have been timed out in **" + event.getGuild().getName() + "**\n"
                                                + "**By:** " + event.getUser().getAsMention() + "**\n"
                                                + "⏱️ **Duration:** " + formatDuration(duration) + "\n"
                                                + "📅 **Expires:** <t:" + timeoutUntil.getEpochSecond() + ":R>\n"
                                                + "❗ **Reason:** " + reason;

                                        privateChannel.sendMessage(dmMessage).queue(
                                                null, // No success handler needed
                                                dmError -> event.getChannel().sendMessage(
                                                        "⚠️ Note: Could not DM " + user.getAsMention() + " (they may have DMs disabled)"
                                                ).queue()
                                        );
                                    },
                                    dmError -> event.getChannel().sendMessage(
                                            "⚠️ Note: Could not DM " + user.getAsMention() + " (DMs closed to server members)"
                                    ).queue()
                            );
                        },
                        error -> event.reply("❌ Failed to timeout " + user.getAsMention()
                                + ": " + error.getMessage()).setEphemeral(true).queue()
                );
            }, error -> {
                event.reply("❌ That user is not in this server!").setEphemeral(true).queue();
            });
        } catch (IllegalArgumentException e) {
            event.reply("❌ Invalid time format. Use formats like: 30m, 2h, 1d").setEphemeral(true).queue();
        }
    }

    private static Duration parseDuration(String input) throws IllegalArgumentException {
        Matcher matcher = TIME_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time format");
        }

        int amount = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" -> Duration.ofSeconds(amount);
            case "m" -> Duration.ofMinutes(amount);
            case "h" -> Duration.ofHours(amount);
            case "d" -> Duration.ofDays(amount);
            default -> throw new IllegalArgumentException("Invalid time unit");
        };
    }

    private static String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();   // Uses Java 9+ toHoursPart()
        long minutes = duration.toMinutesPart(); // Uses Java 9+ toMinutesPart()
        long seconds = duration.toSecondsPart(); // Uses Java 9+ toSecondsPart()

        if (days > 0) {
            return days + " day" + (days != 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " hour" + (hours != 1 ? "s" : "");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        }
    }
}