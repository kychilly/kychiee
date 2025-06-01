package com.kychilly.DiscordBot.listeners;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kychilly.DiscordBot.commands.WordBomb;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Typeracer extends ListenerAdapter {

    private String theRaceText = "";

    private final List<String> wordList;

    public Typeracer() {
        this.wordList = readTextFile("com/github/KychillyBot/wordbomb/dictionary.txt");
    }

    private final Map<Long, String> activeRace = new HashMap<>();
    private final Map<Long, Long> raceStartTime = new HashMap<>();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;  // Ignore the message if it's from a bot
        }

        String content = event.getMessage().getContentRaw();
        long channelId = event.getChannel().getIdLong();

        if (content.equalsIgnoreCase("!typerace")) {
            if (activeRace.containsKey(channelId)) {
                event.getChannel().sendMessage("A race is already in progress!").queue();
                return;
            }

            // Generate 20-word sentence
            StringBuilder sentence = new StringBuilder();
            Random rand = new Random();
            for (int i = 0; i < 20; i++) {
                sentence.append(wordList.get(rand.nextInt(wordList.size()))).append(" ");
            }

            theRaceText = sentence.toString().trim();
            System.out.println("this is real string: " + theRaceText);
            String raceText = toCyrillicHomoglyphs(theRaceText);
            activeRace.put(channelId, raceText);
            raceStartTime.put(channelId, System.currentTimeMillis()+5000);
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

            // Timeout after 60 seconds

            scheduler.schedule(() -> {
                if (!activeRace.containsKey(channelId)) {
                    scheduler.shutdown();
                }
                if (activeRace.containsKey(channelId)) {
                    activeRace.remove(channelId);
                    raceStartTime.remove(channelId);
                    event.getChannel().sendMessage("⌛ The typerace has expired after 60 seconds.").queue();
                }
                scheduler.shutdown();
            }, 1, TimeUnit.MINUTES);
        }

        // Check if someone typed the correct sentence
        else if (activeRace.containsKey(channelId)) {
            //check cyrlciscs
            if (containsCyrillic(content)) {
                event.getChannel().sendMessage("lol u tried to cheat").queue();
                return;
            }
            //System.out.println(theRaceText);

            if (content.equals(theRaceText)) {
                long timeTakenMs = System.currentTimeMillis() - raceStartTime.get(channelId);
                double timeTakenMin = timeTakenMs / 60000.0;
                int wpm = (int) (20 / timeTakenMin);

                activeRace.remove(channelId);
                raceStartTime.remove(channelId);

                event.getChannel().sendMessage("🏆 " + event.getAuthor().getAsMention() + " wins! 🎉\n" +
                        "⏱ Time: " + (timeTakenMs / 1000.0) + " seconds\n" +
                        "⌨️ WPM: " + wpm).queue();
                scheduler.shutdown();
            }
        }
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

    private List<String> readTextFile(String filePath) {
        List<String> wordList = new ArrayList<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            if (inputStream == null) {
                throw new RuntimeException("File not found: " + filePath);
            }

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    wordList.add(line.toLowerCase());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading text file at " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Failed to load word list from " + filePath, e);
        }
        return wordList;
    }

}
