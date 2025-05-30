package com.kychilly.DiscordBot.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kychilly.DiscordBot.commands.TyperacerCommand;
import com.kychilly.DiscordBot.commands.WordBomb;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

public class TyperacerPlayer {
    private final List<String> wordList;
    private final String realText;
    private final String raceText;
    private final long channelId;
    private final ScheduledExecutorService scheduler;
    private long startTime;
    private MessageChannel channel;

    public TyperacerPlayer(SlashCommandInteractionEvent event) {
        this.wordList = decodeJSON("dictionary");
        this.realText = generateSentence(20);
        System.out.println(realText);
        this.raceText = toCyrillicHomoglyphs(realText);
        this.channelId = event.getChannel().getIdLong();
        this.channel = event.getChannel();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // Countdown message
        channel.sendMessage("TypeRacer starting in 5 seconds! Get ready!")
                .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));

        // Schedule race start
        scheduler.schedule(() -> {
            this.startTime = System.currentTimeMillis();
            channel.sendMessage("🏁 **Typeracer Starting!** Type the following 20 words exactly:\n```" + raceText + "```").queue();
        }, 5, TimeUnit.SECONDS);

        // Schedule timeout
        scheduler.schedule(() -> {
            if (TyperacerCommand.getActiveGames().containsKey(channelId)) {
                channel.sendMessage("⌛ Time's up! The race has ended.").queue();
                TyperacerCommand.endGame(channelId);
            }
        }, 65, TimeUnit.SECONDS);
    }

    public ArrayList<String> decodeJSON(String filePath) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();

        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(WordBomb.class.getClassLoader().getResourceAsStream("com/github/KychillyBot/typeracer/" + filePath + ".json")))) {
            return gson.fromJson(reader, listType);
        } catch (Exception ignored) {

            System.out.println("Error decoding JSON at " + filePath);
        }
        throw new RuntimeException(filePath + " invalid path");
    }

    private String generateSentence(int wordCount) {
        StringBuilder sentence = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < wordCount; i++) {
            sentence.append(wordList.get(rand.nextInt(wordList.size()))).append(" ");
        }
        return sentence.toString().trim();
    }

    public void cleanup() {
        scheduler.shutdown();
    }

    // Getters and utility methods
    public String getRealText() { return realText; }
    public long getStartTime() { return startTime; }

    public static String toCyrillicHomoglyphs(String input) {
        Map<Character, Character> homoglyphs = Map.ofEntries(
                Map.entry('a', 'а'), Map.entry('e', 'е'),
                Map.entry('o', 'о'), Map.entry('c', 'с'),
                Map.entry('p', 'р'), Map.entry('x', 'х'),
                Map.entry('y', 'у'), Map.entry('A', 'А'),
                Map.entry('E', 'Е'), Map.entry('O', 'О'),
                Map.entry('C', 'С'), Map.entry('P', 'Р'),
                Map.entry('X', 'Х'), Map.entry('Y', 'У')
        );

        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            result.append(homoglyphs.getOrDefault(ch, ch));
        }
        return result.toString();
    }

    public static boolean containsCyrillic(String input) {
        return input.chars().anyMatch(ch -> ch >= '\u0400' && ch <= '\u04FF');
    }
}