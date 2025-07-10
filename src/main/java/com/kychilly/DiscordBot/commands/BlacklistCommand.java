package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.utils.BlacklistManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BlacklistCommand {
    public static void execute(SlashCommandInteractionEvent event) {
        if (event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("You need the 'Manage Server' permission to use this command.").setEphemeral(true).queue();
            return;
        }

        String word = event.getOption("word").getAsString();

        if (BlacklistManager.getBlacklist(event.getGuild()).contains(word.toLowerCase())) {
            BlacklistManager.removeWord(event.getGuild(), word);
            event.reply("Removed `" + word + "` from the blacklist.").setEphemeral(true).queue();
        } else {
            BlacklistManager.addWord(event.getGuild(), word);
            event.reply("Added `" + word + "` to the blacklist.").setEphemeral(true).queue();
        }
    }

    public static net.dv8tion.jda.api.interactions.commands.build.CommandData getCommandData() {
        return net.dv8tion.jda.api.interactions.commands.build.Commands.slash("blacklist", "Manage blacklisted words")
                .addOptions(
                        new OptionData(OptionType.STRING, "word", "The word to blacklist or unblacklist", true)
                );
    }
}