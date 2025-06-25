package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

//what else can i say?
public class SkibidiCommand {

    public static CommandData getCommandData() {
        return Commands.slash("skibidi", "what else can I say?");
    }

    public static void execute(SlashCommandInteractionEvent event) {
        event.reply("https://tenor.com/view/skibidi-toilet-skibidi-black-white-greyscale-gif-16790784313009810519").queue();
    }

}
