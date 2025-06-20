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
                .addOption(OptionType.STRING, "name", "Channel name", true);
    }

    //i chatgpted this method lol
    public static void execute(SlashCommandInteractionEvent event) {
        // Get the channel name from the slash command option
        String channelName = event.getOption("name").getAsString(); // ✅ Get user input

        // Create the channel with the given name
        event.getGuild().createTextChannel(channelName) // Use the provided name
                .setTopic("Automatically created channel")  // Optional
                .setNSFW(false)                            // Optional
                .addPermissionOverride(
                        event.getGuild().getPublicRole(),
                        EnumSet.of(Permission.VIEW_CHANNEL), // Allow viewing
                        EnumSet.of(Permission.MESSAGE_SEND)  // Deny sending messages
                )
                .queue(
                        channel -> {
                            event.reply("✅ Channel **" + channelName + "** created!").setEphemeral(true).queue();
                            channel.sendMessage("This channel was just created!").queue();
                        },
                        error -> {
                            event.reply("❌ Failed to create channel: " + error.getMessage()).setEphemeral(true).queue();
                        }
                );
    }
}
