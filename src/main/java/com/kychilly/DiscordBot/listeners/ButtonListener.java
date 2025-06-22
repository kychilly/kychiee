package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.classes.MinesweeperGame;
import com.kychilly.DiscordBot.commands.MinesweeperCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        if (buttonId == null) return;

        if (buttonId.startsWith("minesweeper:")) {
            handleMinesweeperButton(event, buttonId);
        }
        // Add other button handlers here if needed
    }

    private void handleMinesweeperButton(ButtonInteractionEvent event, String buttonId) {
        String userId = event.getUser().getId();
        MinesweeperGame game = MinesweeperCommand.getGame(userId);

        // Check if game exists
        if (game == null) {
            event.reply("You don't have an active Minesweeper game! Use `/minesweeper` to start one.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Check if game already ended
        if (game.isGameOver()) {
            event.reply("This game has already ended. Start a new one with `/minesweeper`")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Parse button coordinates
        String[] parts = buttonId.split(":");
        int row = Integer.parseInt(parts[1]);
        int col = Integer.parseInt(parts[2]);

        // Process the move
        game.reveal(col, row);

        boolean wasBomb = !game.reveal(col, row); // reveal() returns false if bomb

        // Handle game over state
        if (game.isGameOver() || game.hasWon()) {
            String resultMessage;
            if (wasBomb) {
                resultMessage = "💥 BOOM! You clicked a bomb at (" + (col+1) + "," + (row+1) + ")!";
            } else {
                resultMessage = "🎉 You won! 🎉";
            }
            handleGameEnd(event, game, userId);
        } else {
            // Update the game board
            updateGameBoard(event, game);
        }
    }

    private void handleGameEnd(ButtonInteractionEvent event, MinesweeperGame game, String userId) {
        // Get the last clicked position from the game
        int clickedCol = game.getLastClickedX();
        int clickedRow = game.getLastClickedY();

        // Create appropriate message
        String resultMessage = game.hasWon()
                ? "🎉 You won! You are now a bomb master!!! 🎉"
                : "💥 BOOM! You clicked a bomb! Better luck next time!";

        // Create final board with explosion marker
        MessageEmbed finalEmbed = createFinalBoardEmbed(game, clickedCol, clickedRow);
        List<ActionRow> finalButtons = createFinalButtons(game, clickedCol, clickedRow);

        //send result in a reply
        event.reply(resultMessage)
                .setEphemeral(false)
                .queue(response -> {
                    event.getHook().editOriginalEmbeds(finalEmbed)
                            .setComponents(finalButtons)
                            .queue();
                });

        // Clean up the game
        MinesweeperCommand.endGame(userId);
    }

    private MessageEmbed createFinalBoardEmbed(MinesweeperGame game, int bombCol, int bombRow) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minesweeper (" + game.getWidth() + "×" + game.getHeight() + ")")
                .setColor(game.hasWon() ? 0x00FF00 : 0xFF0000);

        String[][] board = game.getVisibleBoard();
        for (int y = 0; y < board.length; y++) {
            StringBuilder rowText = new StringBuilder();
            for (int x = 0; x < board[y].length; x++) {
                // Mark the clicked bomb with explosion
                if (!game.hasWon() && x == bombCol && y == bombRow && board[y][x].equals("💣")) {
                    rowText.append("💥");
                } else {
                    rowText.append(board[y][x]);
                }
            }
            embed.addField("", rowText.toString(), false);
        }
        return embed.build();
    }

    private List<ActionRow> createFinalButtons(MinesweeperGame game, int bombCol, int bombRow) {
        List<Button> buttons = new ArrayList<>();
        String[][] board = game.getVisibleBoard();

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                String id = "minesweeper:" + y + ":" + x;
                String display = board[y][x];

                // Mark the clicked bomb with explosion
                if (!game.hasWon() && x == bombCol && y == bombRow && display.equals("💣")) {
                    display = "💥";
                }

                buttons.add(Button.secondary(id, display).asDisabled());
            }
        }

        return splitIntoActionRows(buttons);
    }

    private void updateGameBoard(ButtonInteractionEvent event, MinesweeperGame game) {
        event.editMessageEmbeds(createBoardEmbed(game, false))
                .setComponents(createButtonRows(game))
                .queue();
    }

    private MessageEmbed createBoardEmbed(MinesweeperGame game, boolean isFinal) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minesweeper (" + game.getWidth() + "×" + game.getHeight() + ")")
                .setColor(isFinal ? (game.hasWon() ? 0x00FF00 : 0xFF0000) : 0x00AA00);

        String[][] board = game.getVisibleBoard();
        for (String[] row : board) {
            embed.addField("", String.join("", row), false);
        }

        return embed.build();
    }

    private List<ActionRow> createButtonRows(MinesweeperGame game) {
        List<Button> buttons = new ArrayList<>();
        String[][] board = game.getVisibleBoard();

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                String id = "minesweeper:" + y + ":" + x;
                buttons.add(
                        board[y][x].equals("||❔||")
                                ? Button.primary(id, "\u200B")  // Invisible space
                                : Button.secondary(id, board[y][x]).asDisabled()
                );
            }
        }

        return splitIntoActionRows(buttons);
    }

    private List<ActionRow> createDisabledButtons(MinesweeperGame game) {
        List<Button> buttons = new ArrayList<>();
        String[][] board = game.getVisibleBoard();

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                String id = "minesweeper:" + y + ":" + x;
                buttons.add(Button.secondary(id, board[y][x]).asDisabled());
            }
        }

        return splitIntoActionRows(buttons);
    }

    private List<ActionRow> splitIntoActionRows(List<Button> buttons) {
        List<ActionRow> rows = new ArrayList<>();
        int maxButtonsPerRow = 5;

        for (int i = 0; i < buttons.size(); i += maxButtonsPerRow) {
            int end = Math.min(i + maxButtonsPerRow, buttons.size());
            rows.add(ActionRow.of(buttons.subList(i, end)));
        }

        return rows;
    }
}