package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CoinflipCommand {

    public static CommandData getCommandData() {
        return Commands.slash("coinflip", "flip a coin");
    }

    public static void execute (SlashCommandInteractionEvent event) {
        String s = Math.random() < .5 ? "Your coin landed on: **heads**" : "Your coin landed on: **tails**";
        event.reply(s).queue();
    }

}
