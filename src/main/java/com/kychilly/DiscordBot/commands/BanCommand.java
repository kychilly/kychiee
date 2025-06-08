package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.Result;

import java.util.concurrent.TimeUnit;

public class BanCommand {

    public static CommandData getCommandData() {
        return Commands.slash("ban", "Ban a user")
                .addOption(OptionType.USER, "user", "The user to ban", true)
                .addOption(OptionType.STRING, "reason", "The reason to ban", false);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        // Check if user has permission to ban
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You don't have permission to ban members!").setEphemeral(true).queue();
            return;
        }

        // Get the target user
        User targetUser = event.getOption("user").getAsUser();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        // Check if target is the command executor
        if (targetUser.getIdLong() == event.getMember().getIdLong()) {
            event.reply("You cannot ban yourself!").setEphemeral(true).queue();
            return;
        }

        // Try to get as member (checks if in server)
        event.getGuild().retrieveMember(targetUser).queue(
                target -> {
                    // Check if target is server owner
                    if (target.isOwner()) {
                        event.reply("You cannot ban the server owner!").setEphemeral(true).queue();
                        return;
                    }

                    // Check role hierarchy
                    if (!event.getMember().canInteract(target)) {
                        event.reply("You cannot ban that user (higher/equal role)!").setEphemeral(true).queue();
                        return;
                    }

                    if (!event.getGuild().getSelfMember().canInteract(target)) {
                        event.reply("I can't ban that user (my role is too low)!").setEphemeral(true).queue();
                        return;
                    }

                    // Send DM first
                    sendBanDM(targetUser, event.getGuild(), event.getUser(), reason).queue(
                            dmSuccess -> performBan(event, targetUser, reason, true),
                            dmError -> performBan(event, targetUser, reason, false)
                    );
                },
                // User not in server (can still ban)
                error -> performBan(event, targetUser, reason, false)
        );
    }

    private static void performBan(SlashCommandInteractionEvent event, User target, String reason, boolean dmSuccess) {
        String successMessage = dmSuccess
                ? "Banned " + target.getAsMention() + " \nReason: " + reason
                : "Banned " + target.getAsMention() + " (could not DM them)";

        event.getGuild().ban(target, 0, TimeUnit.SECONDS)
                .reason("Banned by " + event.getUser().getName() + ": " + reason)
                .queue(
                        success -> event.reply(successMessage).queue(),
                        error -> event.reply("Failed to ban: " + error.getMessage()).setEphemeral(true).queue()
                );
    }

    private static RestAction<Void> sendBanDM(User target, Guild guild, User moderator, String reason) {
        String dmMessage = "⚠️ **You have been banned** ⚠️\n\n" +
                "**Server:** " + guild.getName() + "\n" +
                "**Moderator:** " + moderator.getAsMention() + "\n" +
                "**Reason:** " + reason + "\n\n" +
                "Contact the Woodland Mansion support server if you believe this was a mistake\n";

        return target.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(dmMessage))
                .map(msg -> (Void) null); // Convert Message to Void
    }

    // Command registration data
    public static OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "user", "The user to ban", true),
                new OptionData(OptionType.STRING, "reason", "Reason for the ban", false)
        };
    }
}