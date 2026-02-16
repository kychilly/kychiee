package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CoinflipCommand {

    private static final String HEADS_IMG = "https://media.discordapp.net/attachments/1186115783013711894/1473077953863618633/image.png?ex=6994e640&is=699394c0&hm=e2db6cec2ded8922ad29a4064f5058742457028674df79e4c5b976a2c40ff5f3&=&format=webp&quality=lossless&width=227&height=222";
    private static final String TAILS_IMG = "https://media.discordapp.net/attachments/1186115783013711894/1473078031948972163/image.png?ex=6994e653&is=699394d3&hm=89809218560b73df239bda0a5d0c5efe9f9636d436562f16cebec694e02a201b&=&format=webp&quality=lossless&width=216&height=221";

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
                        Thread.sleep(300);
                        isHeads = !isHeads;

                        // Add a random number or timestamp to the URL to bypass cache(I got this from geminiai)
                        String cacheBuster = "?v=" + System.currentTimeMillis() + i;
                        String currentImg = isHeads ? HEADS_IMG : TAILS_IMG;

                        embed.setImage(currentImg + cacheBuster);

                        hook.editOriginalEmbeds(embed.build()).queue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String result = Math.random() < 0.5 ? "HEADS" : "TAILS";
                embed.setTitle("Result: " + result)
                        .setImage(result.equals("HEADS") ? HEADS_IMG : TAILS_IMG)
                        .setColor(0x00FF00);

                hook.editOriginalEmbeds(embed.build()).queue();
            }).start();
        });
    }
}