package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.utils.BlacklistManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RemoveBlacklistCommand {
    public static void execute(SlashCommandInteractionEvent event) {
        // Permission check
        if (event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            if (event.getMember().getIdLong() != 840216337119969301L) {
                event.reply("You need the 'Manage Server' permission to use this command.").setEphemeral(true).queue();
                return;
            }
        }

        String word = event.getOption("word").getAsString().toLowerCase();
        if (BlacklistManager.getBlacklist(event.getGuild()).contains(word)) {
            BlacklistManager.removeWord(event.getGuild(), word);
            event.reply("Removed `" + word + "` from the blacklist.").setEphemeral(false).queue();
        } else {
            event.reply("`" + word + "` is not in the blacklist.").setEphemeral(false).queue();
        }
    }

    public static net.dv8tion.jda.api.interactions.commands.build.CommandData getCommandData() {
        return Commands.slash("removeblacklist", "Remove a word from the server's blacklist")
                .addOptions(
                        new OptionData(OptionType.STRING, "word", "The word to remove from blacklist", true)
                );
    }
}