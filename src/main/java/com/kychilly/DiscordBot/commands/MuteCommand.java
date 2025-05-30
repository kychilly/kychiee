package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class MuteCommand {


    public static CommandData getCommandData() {
        return Commands.slash("mute", "Mutes a user")
                .addOption(OptionType.USER, "user", "The user to kick", true)
                .addOption(OptionType.STRING, "reason", "The reason for muting", false);
    }


    //I honestly have no idea how to code this now lol


}
