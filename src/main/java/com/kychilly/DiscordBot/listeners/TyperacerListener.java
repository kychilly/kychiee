package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.classes.TyperacerPlayer;
import com.kychilly.DiscordBot.commands.TyperacerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TyperacerListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!TyperacerCommand.PlayingTyperacer(event.getChannel().getIdLong())) return;

        String content = event.getMessage().getContentRaw();
        String theRaceText = TyperacerPlayer.getRealText();
        long channelID = event.getChannel().getIdLong();

        if (containsCyrillic(content)) {
            event.getChannel().sendMessage("lol u tried to cheat").queue();
            return;
        }
        if (content.equals(theRaceText)) {
            long timeTakenMs = System.currentTimeMillis() - TyperacerPlayer.getRaceStartTime().get(channelID);
            double timeTakenMin = timeTakenMs / 60000.0;
            int wpm = (int) (20 / timeTakenMin);

            TyperacerCommand.getTyperacerGames().remove(channelID);
            TyperacerPlayer.getRaceStartTime().remove(channelID);

            event.getChannel().sendMessage("🏆 " + event.getAuthor().getAsMention() + " wins! 🎉\n" +
                    "⏱ Time: " + (timeTakenMs / 1000.0) + " seconds\n" +
                    "⌨️ WPM: " + wpm).queue();
            TyperacerPlayer.getScheduler().shutdown();
        }
    }




    public static String toCyrillicHomoglyphs(String input) {
        Map<Character, Character> homoglyphs = Map.ofEntries(
                Map.entry('a', 'а'), // Cyrillic a
                Map.entry('e', 'е'), // Cyrillic e
                Map.entry('o', 'о'), // Cyrillic o
                Map.entry('c', 'с'), // Cyrillic c
                Map.entry('p', 'р'), // Cyrillic p
                Map.entry('x', 'х'), // Cyrillic x
                Map.entry('y', 'у'), // Cyrillic y
                Map.entry('A', 'А'),
                Map.entry('E', 'Е'),
                Map.entry('O', 'О'),
                Map.entry('C', 'С'),
                Map.entry('P', 'Р'),
                Map.entry('X', 'Х'),
                Map.entry('Y', 'У')
        );
        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (homoglyphs.containsKey(ch)) {
                result.append(homoglyphs.get(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public static boolean containsCyrillic(String input) {
        for (char ch : input.toCharArray()) {
            // Cyrillic characters are in the Unicode range U+0400 to U+04FF
            if (ch >= '\u0400' && ch <= '\u04FF') {
                return true;
            }
        }
        return false;
    }
}
