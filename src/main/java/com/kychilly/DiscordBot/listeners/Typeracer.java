package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Typeracer extends ListenerAdapter {

    private String theRaceText = "";

    private final List<String> wordList = List.of(
            "fast", "keyboard", "java", "discord", "race", "bot", "type", "challenge",
            "speed", "winner", "computer", "game", "code", "chat", "message", "channel",
            "quick", "random", "fun", "text", "contest", "start", "finish", "timer"
    );



    private final Map<Long, String> activeRace = new HashMap<>();
    private final Map<Long, Long> raceStartTime = new HashMap<>();

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;  // Ignore the message if it's from a bot
        }
        String content = event.getMessage().getContentRaw();
        long channelId = event.getChannel().getIdLong();

        if (content.equals("!emily")) {
            event.getChannel().sendMessage("<:emily:1327147566311407679>").queue();
        }

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
            raceStartTime.put(channelId, System.currentTimeMillis());

            event.getChannel().sendMessage("🏁 **Typerace Starting!** Type the following 20 words exactly:\n\n" +
                    "```" + raceText + "```").queue();

            // Timeout after 60 seconds
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
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

}
