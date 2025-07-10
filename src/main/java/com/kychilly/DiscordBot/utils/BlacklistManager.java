package com.kychilly.DiscordBot.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlacklistManager {
    private static final String FILE_PATH = "blacklists.json";
    private static final Gson gson = new Gson();
    private static Map<Long, Set<String>> guildBlacklists = new HashMap<>();

    static {
        loadBlacklists();
    }

    private static void loadBlacklists() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<Long, Set<String>>>(){}.getType();
            guildBlacklists = gson.fromJson(reader, type);
            if (guildBlacklists == null) {
                guildBlacklists = new HashMap<>();
            }
        } catch (IOException e) {
            System.out.println("No existing blacklist file found, creating new one.");
            guildBlacklists = new HashMap<>();
        }
    }

    // For returning the list of blacklisted words
    public static String getFormattedBlacklist(Guild guild) {
        Set<String> blacklist = getBlacklist(guild);
        if (blacklist.isEmpty()) {
            return "There are no blacklisted words in this server.";
        }

        StringBuilder sb = new StringBuilder("**Blacklisted words in this server:**\n");
        blacklist.forEach(word -> sb.append("• ").append(word).append("\n"));
        return sb.toString();
    }

    private static void saveBlacklists() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(guildBlacklists, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getBlacklist(Guild guild) {
        return guildBlacklists.computeIfAbsent(guild.getIdLong(), k -> new HashSet<>());
    }

    public static void addWord(Guild guild, String word) {
        Set<String> blacklist = getBlacklist(guild);
        blacklist.add(word.toLowerCase());
        saveBlacklists();
    }

    public static void removeWord(Guild guild, String word) {
        Set<String> blacklist = getBlacklist(guild);
        blacklist.remove(word.toLowerCase());
        saveBlacklists();
    }

    public static boolean containsBlacklistedWord(Guild guild, String message) {
        if (guild == null || message == null) return false;

        Set<String> blacklist = guildBlacklists.get(guild.getIdLong());
        if (blacklist == null || blacklist.isEmpty()) return false;

        String lowerMessage = message.toLowerCase();
        for (String word : blacklist) {
            if (lowerMessage.matches(".*\\b" + word + "\\b.*")) {
                return true;
            }
        }
        return false;
    }
}