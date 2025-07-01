package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

public class RouletteCommand {

    // Maps userId to bullet position (0-5)
    private static final HashMap<String, Integer> chambers = new HashMap<>();

    // Maps userId to survive count
    private static final HashMap<String, Integer> surviveCount = new HashMap<>();

    private static final Random random = new Random();

    public static CommandData getCommandData() {
        return Commands.slash("sigma_roulette", "Play Sigma Roulette!");
    }

    public static void execute(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();

        int bulletPosition = random.nextInt(6);
        chambers.put(userId, bulletPosition);
        surviveCount.put(userId, 0);  // reset survive count when new game starts

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Sigma Roulette")
                .setDescription(event.getUser().getAsMention() + ", you've loaded the revolver.\n\n**Rules:**\n" +
                        "- If you get a **Chicken Jockey** when you fire, you're safe!\n" +
                        "- If you get a **Skibidi Toilet**, you lose the game.\n" +
                        "- You can spin the chamber or fire the revolver.\n" +
                        "Good luck!")
                .setColor(Color.ORANGE);

        event.replyEmbeds(embed.build())
                .addActionRow(
                        Button.primary("roulette_fire_" + userId, "Fire"),
                        Button.secondary("roulette_spin_" + userId, "Spin")
                )
                .queue();
    }

    public static void handleButton(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split("_");

        if (parts.length != 3 || !parts[0].equals("roulette")) return;

        String action = parts[1];
        String userId = parts[2];
        User user = event.getJDA().getUserById(userId);

        if (user == null) {
            event.reply("User not found!").setEphemeral(true).queue();
            return;
        }

        if (!event.getUser().getId().equals(userId)) {
            event.reply("This is not your roulette game!").setEphemeral(true).queue();
            return;
        }

        int currentSurvive = surviveCount.getOrDefault(userId, 0);

        switch (action) {
            case "spin" -> {
                chambers.put(userId, random.nextInt(6));

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Sigma Roulette - Chamber Spun")
                        .setDescription(user.getAsMention() + " spun the chamber. Good luck!")
                        .addField("Times survived", String.valueOf(currentSurvive), false)
                        .setColor(Color.CYAN);

                event.editMessageEmbeds(embed.build())
                        .setActionRow(
                                Button.primary("roulette_fire_" + userId, "Fire"),
                                Button.secondary("roulette_spin_" + userId, "Spin")
                        )
                        .queue();
            }
            case "fire" -> {
                int chance = random.nextInt(6);
                int bulletPosition = chambers.getOrDefault(userId, -1);

                if (chance == bulletPosition) {
                    // Randomize result between chicken jockey (safe) or skibidi toilet (lose)
                    boolean safe = random.nextBoolean(); // 50% chance safe or lose

                    if (safe) {
                        // Chicken jockey - safe, count as survived
                        currentSurvive++;
                        surviveCount.put(userId, currentSurvive);

                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("🐥 Chicken Jockey! You're safe!")
                                .setDescription(user.getAsMention() + " pulled the trigger and a Chicken Jockey saved them!")
                                .addField("Times survived", String.valueOf(currentSurvive), false)
                                .setColor(Color.GREEN);

                        event.editMessageEmbeds(embed.build())
                                .setActionRow(
                                        Button.primary("roulette_fire_" + userId, "Fire"),
                                        Button.secondary("roulette_spin_" + userId, "Spin")
                                )
                                .queue();
                    } else {
                        // Skibidi toilet - lose
                        chambers.remove(userId);
                        surviveCount.remove(userId);

                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("🚽 SKIBIDI SIGMA Toilet! You lost!")
                                .setDescription(user.getAsMention() + " pulled the trigger and got flushed down! Game over.")
                                .addField("Total rounds survived", String.valueOf(currentSurvive), false)
                                .setColor(Color.RED);

                        event.editMessageEmbeds(embed.build())
                                .setActionRow(
                                        Button.primary("roulette_fire_" + userId, "Fire").asDisabled(),
                                        Button.secondary("roulette_spin_" + userId, "Spin").asDisabled()
                                )
                                .queue();
                    }
                } else {
                    // Not bullet chamber, survived normally
                    currentSurvive++;
                    surviveCount.put(userId, currentSurvive);

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("<:chickenjockey:1389470392128639007> CHICKEN JOCKEY!!!")
                            .setDescription(user.getAsMention() + " pulled the trigger and survived.")
                            .addField("Times survived", String.valueOf(currentSurvive), false)
                            .setColor(Color.GREEN);

                    event.editMessageEmbeds(embed.build())
                            .setActionRow(
                                    Button.primary("roulette_fire_" + userId, "Fire"),
                                    Button.secondary("roulette_spin_" + userId, "Spin")
                            )
                            .queue();
                }
            }
        }
    }
}
