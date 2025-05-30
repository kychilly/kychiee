package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.TyperacerCommand;
import com.kychilly.DiscordBot.classes.TyperacerPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class TyperacerListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        long channelId = event.getChannel().getIdLong();
        TyperacerPlayer game = TyperacerCommand.getActiveGames().get(channelId);

        if (game == null) return;

        String content = event.getMessage().getContentRaw();

        if (TyperacerPlayer.containsCyrillic(content)) {
            event.getChannel().sendMessage("No cheating with Cyrillic characters!").queue();
            return;
        }

        if (content.equals(game.getRealText())) {
            long timeTaken = System.currentTimeMillis() - game.getStartTime();
            double wpm = calculateWPM(timeTaken);

            TyperacerCommand.endGame(channelId);
            event.getChannel().sendMessageEmbeds(
                    createWinEmbed(event, timeTaken, wpm)
            ).queue();
        }
    }

    private double calculateWPM(long millis) {
        return (20 / (millis / 60000.0));
    }

    private MessageEmbed createWinEmbed(MessageReceivedEvent event, long timeMillis, double wpm) {
        return new EmbedBuilder()
                .setTitle("🏆 TypeRacer Winner! 🏆")
                .setDescription(String.format(
                        "%s typed the fastest!",
                        event.getAuthor().getAsMention()
                ))
                .setColor(0x00FF00) // Green color
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl()) // User's profile picture
                .addField("⏱ Time", String.format("%.2f seconds", timeMillis / 1000.0), true)
                .addField("⌨️ WPM", String.valueOf((int) Math.round(wpm)), true)
                .setFooter("Congratulations!", event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .build();
    }
}