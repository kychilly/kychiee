package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.utils.BlacklistManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ViewBlacklistCommand {
    public static void execute(SlashCommandInteractionEvent event) {
        // Permission check probably not needed

        event.reply(BlacklistManager.getFormattedBlacklist(event.getGuild())).setEphemeral(false).queue();
    }

    public static net.dv8tion.jda.api.interactions.commands.build.CommandData getCommandData() {
        return Commands.slash("viewblacklist", "View all blacklisted words in this server");
    }
}