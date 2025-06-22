package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.classes.MinesweeperGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinesweeperCommand {
    private static final Map<String, MinesweeperGame> activeGames = new HashMap<>();
    private static final int MAX_BUTTONS_PER_ROW = 5;
    private static final int MAX_ROWS_PER_MESSAGE = 5;

    public static CommandData getCommandData() {
        return Commands.slash("minesweeper", "Start a new Minesweeper game (5x5)")
                ;
    }

    public static void execute(SlashCommandInteractionEvent event) {
        int width = 5;
        int height = 5;
        int bombCount = 5;

        // Validate game parameters
        if (width * height <= bombCount + 1) {
            event.reply("Too many bombs for this board size!").setEphemeral(true).queue();
            return;
        }

        MinesweeperGame game = new MinesweeperGame(width, height, bombCount);
        activeGames.put(event.getUser().getId(), game);

        try {
            List<ActionRow> actionRows = createButtonRows(game);

            if (actionRows.size() > MAX_ROWS_PER_MESSAGE) {
                event.reply("Board is too large to display all buttons at once!")
                        .setEphemeral(true).queue();
                return;
            }

            // Store the original message so we can edit it later
            event.replyEmbeds(createBoardEmbed(game))
                    .addComponents(actionRows)
                    .queue(interactionHook -> {
                        // Store message ID if needed for later editing
                    });
        } catch (IllegalArgumentException e) {
            event.reply("Failed to create game: " + e.getMessage())
                    .setEphemeral(true).queue();
        }
    }

    private static MessageEmbed createBoardEmbed(MinesweeperGame game) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minesweeper (" + game.getWidth() + "×" + game.getHeight() + ")")
                .setDescription("Click the buttons to reveal squares!")
                .setColor(game.isGameOver() ? (game.hasWon() ? 0x00FF00 : 0xFF0000) : 0x00AA00);

        String[][] board = game.getVisibleBoard();
        int lastX = game.getLastClickedX();
        int lastY = game.getLastClickedY();

        for (int y = 0; y < board.length; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < board[y].length; x++) {
                // Highlight last clicked bomb if game is lost
                if (game.isGameOver() && !game.hasWon() && x == lastX && y == lastY && board[y][x].equals("💣")) {
                    row.append("💥"); // Explosion emoji for the losing click
                } else {
                    row.append(board[y][x]);
                }
            }
            embed.addField("", row.toString(), false);
        }
        return embed.build();
    }

    private static List<ActionRow> createButtonRows(MinesweeperGame game) {
        List<Button> buttons = new ArrayList<>();
        String[][] board = game.getVisibleBoard();
        int lastX = game.getLastClickedX();
        int lastY = game.getLastClickedY();

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                String id = "minesweeper:" + y + ":" + x;
                String display = board[y][x];

                // Highlight last clicked bomb if game is lost
                if (game.isGameOver() && !game.hasWon() && x == lastX && y == lastY && display.equals("💣")) {
                    display = "💥"; // Or use "🔴" for different highlight
                }

                buttons.add(createButton(id, display, game.isGameOver()));
            }
        }

        // Split into action rows with max 5 buttons each
        List<ActionRow> rows = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i += MAX_BUTTONS_PER_ROW) {
            int end = Math.min(i + MAX_BUTTONS_PER_ROW, buttons.size());
            rows.add(ActionRow.of(buttons.subList(i, end)));
        }

        return rows;
    }

    private static Button createButton(String id, String display, boolean isGameOver) {
        if (display.equals("||❔||")) {
            return isGameOver ?
                    Button.secondary(id, " ").asDisabled() : // Disable all buttons when game over
                    Button.primary(id, "\u200B"); // Invisible space for empty button
        }
        return Button.secondary(id, display).asDisabled();
    }

    private static List<ActionRow> createDisabledButtons(MinesweeperGame game) {
        List<Button> buttons = new ArrayList<>();
        String[][] board = game.getVisibleBoard();
        int lastX = game.getLastClickedX();
        int lastY = game.getLastClickedY();

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                String id = "minesweeper:" + y + ":" + x;
                String display = board[y][x];

                // Highlight last clicked bomb
                if (!game.hasWon() && x == lastX && y == lastY && board[y][x].equals("💣")) {
                    display = "💥";
                }

                buttons.add(Button.secondary(id, display).asDisabled());
            }
        }
        return splitIntoActionRows(buttons);
    }

    private static List<ActionRow> splitIntoActionRows(List<Button> buttons) {
        List<ActionRow> rows = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i += MAX_BUTTONS_PER_ROW) {
            int end = Math.min(i + MAX_BUTTONS_PER_ROW, buttons.size());
            rows.add(ActionRow.of(buttons.subList(i, end)));
        }
        return rows;
    }

    public static MinesweeperGame getGame(String userId) {
        return activeGames.get(userId);
    }

    public static void endGame(String userId) {
        activeGames.remove(userId);
    }
}