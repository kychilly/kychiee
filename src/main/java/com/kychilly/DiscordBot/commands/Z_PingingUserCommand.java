package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Z_PingingUserCommand {

    public static CommandData getCommandData() {
        return Commands.slash("ping-user", "Ping a discord user(hehe)")
                .addOption(OptionType.USER, "user", "The user you want to ping", true)
                .addOption(OptionType.INTEGER, "times", "How many times do you want to ping them", true)
                .addOption(OptionType.STRING, "message", "What message to send them?", true);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        User user = event.getOption("user").getAsUser();
        int times = event.getOption("times").getAsInt();
        String mensaje = " " + event.getOption("message").getAsString();

        event.reply("Good luck " + user.getGlobalName()).queue();

        for (int i = 0; i < times; i++) {
            event.getChannel().sendMessage(user.getAsMention() + mensaje).queue();
        }

    }

}
