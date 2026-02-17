package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;

public class CoinflipCommand {

    private static final String HEADS_IMG = "https://media.discordapp.net/attachments/1186115783013711894/1473395688288161913/image.png?ex=69960e2a&is=6994bcaa&hm=65f8570bdbc6271ffb432ec8e6ad7a91af888ba280823bfa97be4d637578e1ff&=&format=webp&quality=lossless&width=67&height=67";
    private static final String TAILS_IMG = "https://media.discordapp.net/attachments/1186115783013711894/1473395744479379673/image.png?ex=69960e37&is=6994bcb7&hm=8866c4658c112bea63274f71976d89a53d3efdd55d2fed0808c883339f629636&=&format=webp&quality=lossless&width=67&height=67";

    public static CommandData getCommandData() {
        return Commands.slash("coinflip", "Flip a coin with an animation!");
    }

    public static void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Flipping...")
                .setImage(HEADS_IMG);

        event.replyEmbeds(embed.build()).queue(hook -> {
            int flips = (int) (Math.random() * 2) + 7;

            new Thread(() -> {
                boolean isHeads = true;

                for (int i = 0; i < flips; i++) {
                    try {
                        Thread.sleep(500);
                        isHeads = !isHeads;

                        // Add a random number or timestamp to the URL to bypass cache(I got this from geminiai)
                        String cacheBuster = "?v=" + System.currentTimeMillis() + i;
                        String currentImg = isHeads ? HEADS_IMG : TAILS_IMG;
                        Color color = isHeads ? Color.getColor("Green") : Color.getColor("Red");


                        embed.setImage(currentImg + cacheBuster);
                        embed.setColor(color);
                        embed.setFooter(isHeads ? "dop dop dop yes yes": "tung tung tung tung sahur");

                        hook.editOriginalEmbeds(embed.build()).queue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String result = Math.random() < 0.5 ? "HEADS" : "TAILS";
                embed.setTitle("Result: " + result)
                        .setImage(result.equals("HEADS") ? HEADS_IMG : TAILS_IMG)
                        .setColor(0x00FF00)
                        .setFooter(result.equals("HEADS") ? "Skibidi sigma, and out popped a toilet" : "tralalero tralala, tung tung tung tung sahur");

                hook.editOriginalEmbeds(embed.build()).queue();
            }).start();
        });
    }
}