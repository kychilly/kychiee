package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class KickCommand {

    public static CommandData getCommandData() {
        return Commands.slash("kick", "Kick a user from the server")
                .addOption(OptionType.USER, "user", "The user to kick", true)
                .addOption(OptionType.STRING, "reason", "The reason for kicking", false);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        // Check if user has permission to kick
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("You don't have permission to kick members!").setEphemeral(true).queue();
            return;
        }

        // Get the target user
        OptionMapping targetOption = event.getOption("user");
        if (targetOption == null) {
            event.reply("Please specify a user to kick!").setEphemeral(true).queue();
            return;
        }

        Member target = targetOption.getAsMember();
        if (target == null) {
            event.reply("That user is not in this server!").setEphemeral(true).queue();
            return;
        }

        // Check if target is the command executor
        if (target.getIdLong() == event.getMember().getIdLong()) {
            event.reply("You cannot kick yourself!").setEphemeral(true).queue();
            return;
        }

        // Check if target is the server owner
        if (target.isOwner()) {
            event.reply("You cannot kick the server owner!").setEphemeral(true).queue();
            return;
        }

        // Check if bot can interact with target
        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("I cannot kick that user because they have a higher role than me!").setEphemeral(true).queue();
            return;
        }

        // Check if executor can interact with target (role hierarchy check)
        if (!event.getMember().canInteract(target)) {
            event.reply("You cannot kick that user because they have a higher or equal role to you!").setEphemeral(true).queue();
            return;
        }

        // Get reason (optional)
        String reason = "No reason provided";
        OptionMapping reasonOption = event.getOption("reason");
        if (reasonOption != null) {
            reason = reasonOption.getAsString();
        }

        String theReason = event.getOption("reason", () -> "No reason provided", OptionMapping::getAsString);

        // Kick the user
        target.kick()
                .reason("Kicked by " + event.getUser().getName() + ": " + reason)
                .queue(
                        success -> event.reply("Kicked " + target.getAsMention() + " successfully!\nReason: " + theReason).queue(),
                        error -> event.reply("Failed to kick user: " + error.getMessage()).setEphemeral(true).queue()
                );
    }

    // Command registration data
    public static OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "user", "The user to kick", true),
                new OptionData(OptionType.STRING, "reason", "Reason for the kick", false)
        };
    }
}