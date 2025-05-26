package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

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
        OptionMapping targetOption = event.getOption("user");
        if (targetOption == null) {
            event.reply("Please specify a user to ban!").setEphemeral(true).queue();
            return;
        }

        Member target = targetOption.getAsMember();
        if (target == null) {
            event.reply("That user is not in this server!").setEphemeral(true).queue();
            return;
        }

        // Check if target is bannable
        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("I cannot ban that user because they have a higher role than me!").setEphemeral(true).queue();
            return;
        }

        // Get reason (optional)
        String reason = "No reason provided";
        OptionMapping reasonOption = event.getOption("reason");
        if (reasonOption != null) {
            reason = reasonOption.getAsString();
        }

        // Ban the user
        event.getGuild().ban(target, 0, TimeUnit.SECONDS)
                .reason("Banned by " + event.getUser().getName() + ": " + reason)
                .queue(
                        success -> event.reply("Banned " + target.getAsMention() + " successfully!").queue(),
                        error -> event.reply("Failed to ban user: " + error.getMessage()).setEphemeral(true).queue()
                );
    }

    // Command registration data
    public static OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "user", "The user to ban", true),
                new OptionData(OptionType.STRING, "reason", "Reason for the ban", false)
        };
    }
}