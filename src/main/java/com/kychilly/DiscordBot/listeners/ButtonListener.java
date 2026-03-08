package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.classes.MinesweeperGame;
import com.kychilly.DiscordBot.commands.MinesweeperCommand;
import com.kychilly.DiscordBot.utils.UsersManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kychilly.DiscordBot.commands.UsersShowCommand.cachedUserLists;

public class ButtonListener extends ListenerAdapter {

    private static final int USERS_PER_PAGE = 10;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {


        String buttonId = event.getButton().getId();
        if (buttonId == null) return;

        if (buttonId.startsWith("minesweeper:")) {
            handleMinesweeperButton(event, buttonId);
        }
        if (buttonId.startsWith("users_")) {
            handleUsersButton(event);
        }
        // Add other button handlers here if needed
    }

    public static void handleUsersButton(ButtonInteractionEvent event) {

        if (!event.getComponentId().startsWith("users_"))
            return;

        Guild guild = event.getGuild();
        List<Long> users = cachedUserLists.get(guild.getIdLong());

        if (users == null) {
            event.reply("Session expired. Run /showusers again.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int currentPage = Integer.parseInt(event.getComponentId().split("_")[2]);
        int newPage = currentPage;

        if (event.getComponentId().startsWith("users_next"))
            newPage++;

        if (event.getComponentId().startsWith("users_prev"))
            newPage--;

        int maxPage = (int) Math.ceil(users.size() / (double) USERS_PER_PAGE);
        if (maxPage == 0) maxPage = 1;

        newPage = Math.max(0, Math.min(newPage, maxPage - 1));

        int start = newPage * USERS_PER_PAGE;
        int end = Math.min(start + USERS_PER_PAGE, users.size());

        StringBuilder builder = new StringBuilder();
        builder.append("**Total Users:** ").append(users.size()).append("\n\n");

        for (int i = start; i < end; i++) {
            builder.append("<@").append(users.get(i)).append(">\n");
        }

        builder.append("\nPage ").append(newPage + 1).append(" / ").append(maxPage);

        Button prev = Button.primary("users_prev_" + newPage, "◀")
                .withDisabled(newPage == 0);

        Button next = Button.primary("users_next_" + newPage, "▶")
                .withDisabled(newPage >= maxPage - 1);

        event.editMessage(builder.toString())
                .setActionRow(prev, next)
                .queue();
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