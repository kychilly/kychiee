package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class SixSevenCommand {

    public static CommandData getCommandData() {
        return Commands.slash("sixseven", "6767676767767676767676767")
                .addOption(OptionType.USER, "user", "the user you want to 67", true);
    }

    public static void execute(SlashCommandInteractionEvent event) {

        int channelIndex = event.getGuild()
                .getTextChannels()
                .indexOf(event.getChannel()) + 1;
        event.reply("ok").setEphemeral(true).queue();

        TextChannel channel = null;
        String user = event.getOption("user").getAsUser().getAsMention();

        for (int i = 1; i < 7; i++) {
            event.getChannel().sendMessage(user + " " + i).queue();
        }
        event.getChannel().sendMessage("https://tenor.com/view/67-gif-8575841764206736991").queue();
        for (int i = 1; i < 8; i++) {
            channel = event.getGuild().getTextChannels().get(channelIndex);
            channel.sendMessage(user + " " + i).queue();
        }
        channel.sendMessage("https://tenor.com/view/67-gif-8575841764206736991").queue();

    }

}
