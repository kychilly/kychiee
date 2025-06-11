package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class MuteCommand {


    public static CommandData getCommandData() {
        return Commands.slash("mute", "Mutes a user")
                .addOption(OptionType.USER, "user", "The user to kick", true)
                .addOption(OptionType.STRING, "reason", "The reason for muting", false);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("You don't have permission to timeout members!").setEphemeral(true).queue();
            return;
        }
        OptionMapping targetOption = event.getOption("user");

        if (targetOption == null) {
            event.reply("Please specify a user to mute!").setEphemeral(true).queue();
            return;
        }

        Member target = targetOption.getAsMember();
        if (target == null) {
            event.reply("That user is not in this server!").setEphemeral(true).queue();
            return;
        }

        // Check if target is the command executor
        if (target.getIdLong() == event.getMember().getIdLong()) {
            event.reply("You cannot mute yourself!").setEphemeral(true).queue();
            return;
        }

        // Check if target is the server owner
        if (target.isOwner()) {
            event.reply("You cannot mute the server owner!").setEphemeral(true).queue();
            return;
        }

        // Check if bot can interact with target
        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("I cannot mute that user because they have a higher role than me!").setEphemeral(true).queue();
            return;
        }

        // Check if executor can interact with target (role hierarchy check)
        if (!event.getMember().canInteract(target)) {
            event.reply("You cannot mute that user because they have a higher or equal role to you!").setEphemeral(true).queue();
            return;
        }

        // Get reason (optional)
        String reason = "No reason provided";
        OptionMapping reasonOption = event.getOption("reason");
        if (reasonOption != null) {
            reason = reasonOption.getAsString();
        }

        String theReason = event.getOption("reason", () -> "No reason provided", OptionMapping::getAsString);

//        // Mute user
//        target.mute()
//                .reason("Kicked by " + event.getUser().getName() + ": " + reason)
//                .queue(
//                        success -> event.reply("Kicked " + target.getAsMention() + " successfully!\nReason: " + theReason).queue(),
//                        error -> event.reply("Failed to kick user: " + error.getMessage()).setEphemeral(true).queue()
//                );

    }

    public static void HandleCommand(SlashCommandInteraction event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("You don't have permission to timeout members!").setEphemeral(true).queue();
            return;
        }

        if (event.getUser().getIdLong() == 840216337119969301L) {//kyche admin abuse incoming

        }
    }
    
    //I honestly have no idea how to code this now lol


}
