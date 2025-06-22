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

        // Handle game over state
        if (game.isGameOver() || game.hasWon()) {
            handleGameEnd(event, game, userId);
        } else {
            // Update the game board
            updateGameBoard(event, game);
        }
    }

    private void handleGameEnd(ButtonInteractionEvent event, MinesweeperGame game, String userId) {
        String resultMessage = game.hasWon() ? "🎉 You won! 🎉" : "💥 BOOM! You hit a bomb! 💥";

        // Create final board display
        MessageEmbed finalEmbed = createBoardEmbed(game, game.hasWon());
        List<ActionRow> finalButtons = createDisabledButtons(game);

        // Send result and update board
        event.reply(resultMessage).queue(response -> {
            event.getHook().editOriginalEmbeds(finalEmbed)
                    .setComponents(finalButtons)
                    .queue();
        });

        // Clean up the game
        MinesweeperCommand.endGame(userId);
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