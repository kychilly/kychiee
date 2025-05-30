package com.kychilly.DiscordBot.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kychilly.DiscordBot.commands.WordBomb;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.kychilly.DiscordBot.commands.TyperacerCommand.getTyperacerGames;

public class TyperacerPlayer {

    static ArrayList<String> wordList = decodeJSON("com/github/KychillyBot/wordbomb/dictionary.txt");
    static StringBuilder sentence = new StringBuilder();
    static String realText = getSentence();
    String raceText = toCyrillicHomoglyphs(realText);
    static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    long channelId;
    static Map<Long, Long> raceStartTime = new HashMap<>();

    public TyperacerPlayer(SlashCommandInteraction event) {
        channelId = event.getChannelIdLong();

        try {
            event.getChannel().sendMessage("Typeracer starting in 5 seconds! Get ready!")
                    .queue(message -> {
                        // Schedule deletion after 5 seconds
                        message.delete().queueAfter(5, TimeUnit.SECONDS,
                                success -> {},
                                error -> System.out.println("Couldn't delete message: " + error.getMessage())
                        );
                    });
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        event.getChannel().sendMessage("🏁 **Typerace Starting!** Type the following 20 words exactly:\n\n" +
                "```" + raceText + "```").queue();

        scheduler.schedule(() -> {
            if (!getTyperacerGames().containsKey(channelId)) {
                scheduler.shutdown();
            }
            if (getTyperacerGames().containsKey(channelId)) {
                getTyperacerGames().remove(channelId);
                raceStartTime.remove(channelId);
                event.getChannel().sendMessage("⌛ The typerace has expired after 60 seconds.").queue();
            }
            scheduler.shutdown();
        }, 1, TimeUnit.MINUTES);
    }

    public static String getRealText() {
        return realText;
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static Map<Long, Long> getRaceStartTime() {
        return raceStartTime;
    }


    public static String toCyrillicHomoglyphs(String input) {
        Map<Character, Character> homoglyphs = Map.ofEntries(
                Map.entry('a', 'а'), // Cyrillic a
                Map.entry('e', 'е'), // Cyrillic e
                Map.entry('o', 'о'), // Cyrillic o
                Map.entry('c', 'с'), // Cyrillic c
                Map.entry('p', 'р'), // Cyrillic p
                Map.entry('x', 'х'), // Cyrillic x
                Map.entry('y', 'у'), // Cyrillic y
                Map.entry('A', 'А'),
                Map.entry('E', 'Е'),
                Map.entry('O', 'О'),
                Map.entry('C', 'С'),
                Map.entry('P', 'Р'),
                Map.entry('X', 'Х'),
                Map.entry('Y', 'У')
        );
        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (homoglyphs.containsKey(ch)) {
                result.append(homoglyphs.get(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public static boolean containsCyrillic(String input) {
        for (char ch : input.toCharArray()) {
            // Cyrillic characters are in the Unicode range U+0400 to U+04FF
            if (ch >= '\u0400' && ch <= '\u04FF') {
                return true;
            }
        }
        return false;
    }

    public static String getSentence() {
        for (int i = 0; i < 20; i++) {
            sentence.append(wordList.get((int)(Math.random()*wordList.size()))).append(" ");
        }
        return sentence.toString().trim();
    }

    public static ArrayList<String> decodeJSON(String filePath) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();

        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(WordBomb.class.getClassLoader().getResourceAsStream("com/github/KychillyBot/wordbomb/dictionary.txt.json")));
        {
            return gson.fromJson(reader, listType);
        }
    }

}
