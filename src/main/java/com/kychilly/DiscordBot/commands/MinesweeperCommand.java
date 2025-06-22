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
        return Commands.slash("minesweeper", "Start a new Minesweeper game")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "width", "Width of the board (3-10)", false)
                                .setMinValue(3).setMaxValue(10),
                        new OptionData(OptionType.INTEGER, "height", "Height of the board (3-10)", false)
                                .setMinValue(3).setMaxValue(10),
                        new OptionData(OptionType.INTEGER, "bombs", "Number of bombs (1-20)", false)
                                .setMinValue(1).setMaxValue(20)
                );
    }

    public static void execute(SlashCommandInteractionEvent event) {
        int width = event.getOption("width", 5, OptionMapping::getAsInt);
        int height = event.getOption("height", 5, OptionMapping::getAsInt);
        int bombCount = event.getOption("bombs", 5, OptionMapping::getAsInt);

        // Validate game parameters
        if (width * height <= bombCount + 1) {
            event.reply("Too many bombs for this board size!").setEphemeral(true).queue();
            return;
        }

        MinesweeperGame game = new MinesweeperGame(width, height, bombCount);
        activeGames.put(event.getUser().getId(), game);

        try {
            List<ActionRow> actionRows = createButtonRows(game);

            // Discord allows max 5 action rows per message
            if (actionRows.size() > MAX_ROWS_PER_MESSAGE) {
                event.reply("Board is too large to display all buttons at once!")
                        .setEphemeral(true).queue();
                return;
            }

            event.replyEmbeds(createBoardEmbed(game))
                    .addComponents(actionRows)
                    .queue();
        } catch (IllegalArgumentException e) {
            event.reply("Failed to create game: " + e.getMessage())
                    .setEphemeral(true).queue();
        }
    }

    private static MessageEmbed createBoardEmbed(MinesweeperGame game) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minesweeper (" + game.getWidth() + "×" + game.getHeight() + ")")
                .setDescription("Click the buttons to reveal squares!")
                .setColor(0x00AA00);

        String[][] board = game.getVisibleBoard();
        for (String[] row : board) {
            embed.addField("", String.join("", row), false);
        }
        return embed.build();
    }

    private static List<ActionRow> createButtonRows(MinesweeperGame game) {
        List<Button> buttons = new ArrayList<>();
        String[][] board = game.getVisibleBoard();

        // Create buttons in row-major order
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                String id = "minesweeper:" + y + ":" + x;
                buttons.add(createButton(id, board[y][x]));
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

    private static Button createButton(String id, String display) {
        return display.equals("||❔||")
                ? Button.primary(id, "\u200B")  // Invisible space for empty button
                : Button.secondary(id, display).asDisabled();
    }

    public static MinesweeperGame getGame(String userId) {
        return activeGames.get(userId);
    }

    public static void endGame(String userId) {
        activeGames.remove(userId);
    }
}