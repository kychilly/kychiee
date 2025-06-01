package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
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

    public static void HandleCommand(SlashCommandInteraction event) {
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("You don't have permission to timeout members!").setEphemeral(true).queue();
            return;
        }
    }
    
    //I honestly have no idea how to code this now lol


}
