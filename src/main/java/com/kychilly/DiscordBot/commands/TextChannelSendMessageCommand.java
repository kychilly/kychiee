package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;

public class TextChannelSendMessageCommand {

    public static CommandData getCommandData() {
        return Commands.slash("send-message", "Send a message in a channel!")
                .addOption(OptionType.STRING, "message", "WHAT DO U WANT TO SEND", true)
                .addOption(OptionType.STRING, "channel-name", "WAHT CHANNEL U WANNA SEND MESSAGE IN", true)
                .addOption(OptionType.INTEGER, "repeat", "HOW MANY TIMES U WANNA REPEAT MESSAGE", false);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getIdLong() != 840216337119969301L) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You do not have perms lol").setEphemeral(true).queue();
                return;
            }
        }

        String channelName = Objects.requireNonNull(event.getOption("channel-name")).getAsString();
        int repetitions = event.getOption("repeat") != null ? event.getOption("repeat").getAsInt() : 1;

        GuildMessageChannel channelToSendMessage = event.getGuild()
                .getChannels()
                .stream()
                .filter(c -> c instanceof GuildMessageChannel)
                .map(c -> (GuildMessageChannel) c)
                .filter(c -> c.getName().equalsIgnoreCase(channelName))
                .findFirst()
                .orElse(null);

        if (channelToSendMessage == null) {
            event.reply("❌ Could not find a text-like message channel with the name **" + channelName + "**").setEphemeral(true).queue();
            return;
        }

        event.reply("lol ok").queue();

        for (int i = 0; i < repetitions && i < 50; i++) {
            channelToSendMessage.sendMessage(event.getOption("message").getAsString()).queue();
        }
        channelToSendMessage.sendMessage(":D").queue();

    }



}
