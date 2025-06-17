package com.kychilly.DiscordBot.commands;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ChangeNicknameCommand {

    public static CommandData getCommandData() {
        return (Commands.slash("change-nickname", "Change a user's nickname")
                .addOption(OptionType.USER, "user", "The user whos name you want to change")
                .addOption(OptionType.STRING, "nickname", "The nickname you want to change to"));

    }

    public static void execute(SlashCommandInteractionEvent event) {
        Member requester = event.getMember();
        if (!requester.hasPermission(Permission.NICKNAME_CHANGE)) {
            event.reply("You dont have permissions to use this command!").setEphemeral(true).queue();
            return;
        }

        // Get the target user and new nickname
        Member target = event.getOption("user").getAsMember();
        String newNickname = event.getOption("nickname").getAsString();

        // Check if the target exists(PLEASE WORK)
        if (target == null) {
            event.reply("That user is not in this server!").setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("I don't have permission to change this user's nickname!").setEphemeral(true).queue();
            return;
        }

        // If name is too long
        if (newNickname.length() > 32) {
            event.reply("Nicknames can't be longer than 32 characters!").setEphemeral(true).queue();
            return;
        }

        // Change the nickname
        event.getGuild().modifyNickname(target, newNickname).queue(
                success -> event.reply("Successfully changed " + target.getUser().getAsMention() + "'s nickname to: " + newNickname).queue(),
                error -> event.reply("Failed to change nickname: " + error.getMessage()).setEphemeral(true).queue()
        );

    }


}
