package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ShutdownCommand {


    public static CommandData getCommandData() {
        return Commands.slash("shutdown", "(Owner only) Shuts bot down");
    }

    public static void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getIdLong() == 840216337119969301L) {
            event.reply("I am shutting down now. Bye byes D:").queue();
            event.getJDA().shutdown();
        } else {
            event.reply("You don't have permission to use this command!").setEphemeral(true).queue();
        }
    }
}
