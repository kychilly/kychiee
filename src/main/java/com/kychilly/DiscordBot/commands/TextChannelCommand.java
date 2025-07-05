package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class TextChannelCommand {


    public static CommandData getCommandData() {
        return Commands.slash("channel", "Creates a new text channel(in development)")
                .addOption(OptionType.STRING, "name", "Channel name", true)
                .addOption(OptionType.INTEGER, "channel_num", "Channel Index", false);
    }

    //modified to not be chatgpt
    public static void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getIdLong() != 840216337119969301L) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You do not have perms lol").setEphemeral(true).queue();
            }
        }
        String channelName = event.getOption("name").getAsString() != null ? event.getOption("name").getAsString() : "New Channel";
        int channelIndex = event.getOption("channel_num").getAsInt();
        event.getGuild().createTextChannel(channelName)
                .setTopic("New Channel Created!")
                .setPosition(channelIndex)
                .addPermissionOverride(
                        event.getGuild().getPublicRole(),
                        EnumSet.of(Permission.VIEW_CHANNEL), // Allow viewing
                        EnumSet.of(Permission.MESSAGE_SEND)  // Deny sending messages
                )
                .queue(
                        channel -> {
                            event.reply("✅ Channel **" + channelName + "** created!").setEphemeral(false).queue();
                            channel.sendMessage("This channel was just created!").queue();
                        },
                        error -> {
                            event.reply("❌ Failed to create channel: " + error.getMessage()).setEphemeral(true).queue();
                        }
                );
    }
}
