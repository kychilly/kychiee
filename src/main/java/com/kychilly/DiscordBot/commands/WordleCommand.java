package com.kychilly.DiscordBot.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WordleCommand {
    private static final int MAX_ATTEMPTS = 6;
    private static final Map<Long, GameState> activeGames = new HashMap<>();
    private static ArrayList<String> wordList = null;

    // Command data for registration
    public static CommandData getCommandData() {
        return Commands.slash("wordle", "Start a new Wordle game");
    }

    // Static initializer to load words once
    static {
        try {
            wordList = loadWords();
        } catch (Exception e) {
            System.err.println("Failed to load Wordle words: " + e.getMessage());
            wordList = new ArrayList<>(); // Fallback empty list
        }
    }

    // Word loading logic
    private static ArrayList<String> loadWords() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        String path = "com/github/KychillyBot/wordle/5letterWords.json";

        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(WordleCommand.class.getClassLoader().getResourceAsStream(path)))) {
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load word list", e);
        }
    }

    // Main game handler
    public static void handleCommand(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        if (activeGames.containsKey(user.getIdLong())) {
            event.reply("You already have an active game! Finish it first.").setEphemeral(true).queue();
            return;
        }

        String targetWord = wordList.get((int)(Math.random() * wordList.size())).toUpperCase();
        activeGames.put(user.getIdLong(), new GameState(targetWord));

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Wordle!!!")
                .setImage("https://static0.gamerantimages.com/wordpress/wp-content/uploads/2022/03/Wordle-(1).jpg")
                .setDescription("You have " + MAX_ATTEMPTS + " attempts to guess the 5-letter word!")
                .addField("How to Play", "Use `/guess [word]` to make a guess", false)
                .setFooter(event.getUser().getEffectiveName(), event.getUser().getAvatarUrl())
                .setColor(0x5865F2);

        event.replyEmbeds(embed.build())
                .addActionRow(
                        Button.secondary("wordle:hint", "Get Hint"),
                        Button.danger("wordle:quit", "Quit Game")
                ).queue();
    }

    // Guess handler
    public static void handleGuess(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        String guess = event.getOption("word").getAsString().toLowerCase();

        if (!activeGames.containsKey(user.getIdLong())) {
            event.reply("You don't have an active game! Start one with `/wordle`").setEphemeral(true).queue();
            return;
        }

        GameState game = activeGames.get(user.getIdLong());

        if (guess.length() != 5) {
            event.reply("Guess must be exactly 5 letters!").setEphemeral(true).queue();
            return;
        }

        String result = game.processGuess(guess);
        String emojiResult = game.getEmojiResult();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Wordle - Attempt " + game.getAttempts() + "/" + MAX_ATTEMPTS)
                .setDescription(emojiResult)
                .addField("Your Guess", result, false)
                .setColor(0xFEE75C);

        if (game.isWon() || game.isGameOver()) {
            embed.setDescription(emojiResult + "\n\n" + (game.isWon() ? "🎉 You won!! Sharkie is so happy for you :D" : "You lose!! Sharkie is so disappointed in you D:"))
                    .addField("The word was", game.getTargetWord(), false)
                    .setFooter("Brought to you by kyche", event.getUser().getAvatarUrl())
                    .setColor(game.isWon() ? 0x57F287 : 0xED4245);

            activeGames.remove(user.getIdLong());
        }

        event.replyEmbeds(embed.build()).queue();
    }

    // Game state class
    private static class GameState {
        private final String targetWord;
        private int attempts = 0;
        private boolean won = false;
        private final StringBuilder emojiResult = new StringBuilder();

        public GameState(String targetWord) {
            this.targetWord = targetWord;
            System.out.println(targetWord);
        }

        public String processGuess(String guess) {
            attempts++;
            StringBuilder result = new StringBuilder();
            guess = guess.toUpperCase();

            for (int i = 0; i < 5; i++) {
                char g = guess.charAt(i);
                char t = targetWord.charAt(i);

                if (g == t) {
                    result.append("**").append(g).append("** ");
                    emojiResult.append("🟩 ");
                } else if (targetWord.contains(String.valueOf(g))) {
                    result.append(g).append(" ");
                    emojiResult.append("🟨 ");
                } else {
                    result.append("_").append(g).append("_ ");
                    emojiResult.append("⬛ ");
                }
            }

            emojiResult.append("\n");
            won = guess.equals(targetWord);
            return result.toString();
        }

        public String getEmojiResult() { return emojiResult.toString(); }
        public int getAttempts() { return attempts; }
        public String getTargetWord() { return targetWord; }
        public boolean isWon() { return won; }
        public boolean isGameOver() { return won || attempts >= MAX_ATTEMPTS; }
    }

    // Button handlers
    public static void handleHint(User user) {
        if (!activeGames.containsKey(user.getIdLong())) return;

        GameState game = activeGames.get(user.getIdLong());
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage("Wordle hint: " + game.getTargetWord().charAt(0) + "____").queue();
        });
    }

    public static void handleQuit(User user) {
        if (!activeGames.containsKey(user.getIdLong())) return;

        GameState game = activeGames.remove(user.getIdLong());
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage("You quit. The word was: " + game.getTargetWord()).queue();
        });
    }
}