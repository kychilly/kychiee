package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.awt.*;

public class CommandsListCommand {

    public static void execute(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue(); // Acknowledge interaction first

        event.getGuild().retrieveCommands().queue(commands -> {
            if (commands.isEmpty()) {
                event.getHook().sendMessage("No commands found.").queue();
                return;
            }

            // Build a formatted list of commands
            StringBuilder commandList = new StringBuilder();
            for (Command cmd : commands) {
                commandList.append("**/").append(cmd.getName()).append("** - ")
                        .append(cmd.getDescription() != null ? cmd.getDescription() : "No description")
                        .append("\n");
            }

            // Create the embed
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Available Commands (" + commands.size() + ")")
                    .setDescription(commandList.toString())
                    .setColor(Color.CYAN);

            // Send the embed
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }, error -> {
            event.getHook().sendMessage("Failed to retrieve commands: " + error.getMessage()).queue();
        });
    }
}