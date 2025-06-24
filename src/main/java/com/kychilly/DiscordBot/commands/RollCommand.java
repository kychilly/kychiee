package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RollCommand {

    public static CommandData getCommandData() {
        return Commands.slash("roll", "Roll a dice lol")
                .addOption(OptionType.INTEGER, "number", "1 through number", true);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        try {
            int num = (int) (Math.random() * event.getOption("number").getAsInt());
            if (num < 1) {
                event.reply("**" + num + "**" + " is not a value number for this!!! Please input an integer between **1** and **2,147,483,647**").queue();
                return;
            }
            event.reply("YOU ROLLED A **" + num + "** :robot::game_die:!!!").queue();
        } catch (Exception e) {
            event.reply("Please input an integer between 1 and 2,147,483,647").queue();
        }
    }


}
