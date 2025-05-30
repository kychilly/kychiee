package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.TyperacerCommand;
import com.kychilly.DiscordBot.classes.TyperacerPlayer;
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
            event.getChannel().sendMessage(createWinMessage(event.getAuthor().getAsMention(), timeTaken, wpm)).queue();
        }
    }

    private double calculateWPM(long millis) {
        return (20 / (millis / 60000.0));
    }

    private String createWinMessage(String user, long timeMillis, double wpm) {
        return String.format(
                "🏆 %s wins! 🎉\n⏱ Time: %.2f seconds\n⌨️ WPM: %d",
                user,
                timeMillis / 1000.0,
                (int) Math.round(wpm)
        );
    }
}