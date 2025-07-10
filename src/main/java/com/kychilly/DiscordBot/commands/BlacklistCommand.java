package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlacklistCommand {
    // Static map to store blacklisted words per guild
    private static final Map<Long, Set<String>> guildBlacklists = new HashMap<>();

    public static void execute(SlashCommandInteractionEvent event) {
        // Permission check
        if (event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("You need the 'Manage Server' permission to use this command.").setEphemeral(true).queue();
            return;
        }

        String word = event.getOption("word").getAsString().toLowerCase();
        Guild guild = event.getGuild();
        long guildId = guild.getIdLong();

        // Initialize blacklist for guild if not exists
        Set<String> blacklist = guildBlacklists.computeIfAbsent(guildId, k -> new HashSet<>());

        if (blacklist.contains(word)) {
            blacklist.remove(word);
            event.reply("Removed `" + word + "` from the blacklist.").setEphemeral(true).queue();
        } else {
            blacklist.add(word);
            event.reply("Added `" + word + "` to the blacklist.").setEphemeral(true).queue();
        }
    }

    public static net.dv8tion.jda.api.interactions.commands.build.CommandData getCommandData() {
        return net.dv8tion.jda.api.interactions.commands.build.Commands.slash("blacklist", "Manage blacklisted words in this server")
                .addOptions(
                        new OptionData(OptionType.STRING, "word", "The word to blacklist or unblacklist", true)
                );
    }

    // Method to check if a message contains blacklisted words
    public static boolean containsBlacklistedWord(Guild guild, String message) {
        if (guild == null || message == null) return false;

        Set<String> blacklist = guildBlacklists.get(guild.getIdLong());
        if (blacklist == null || blacklist.isEmpty()) return false;

        String lowerMessage = message.toLowerCase();

        // Check for exact word matches (not substrings)
        for (String word : blacklist) {
            // Regex that matches whole words only (not parts of other words)
            if (lowerMessage.matches(".*\\b" + word + "\\b.*")) {
                return true;
            }
        }
        return false;
    }
}