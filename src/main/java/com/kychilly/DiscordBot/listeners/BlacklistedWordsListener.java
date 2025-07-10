package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.BlacklistCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BlacklistedWordsListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Skip messages from bots
        if (event.getAuthor().isBot()) return;

        // Check for blacklisted words
        if (event.getGuild() != null && event.getMessage().getContentRaw() != null) {
            String message = event.getMessage().getContentRaw();
            if (BlacklistCommand.containsBlacklistedWord(event.getGuild(), message)) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(
                        event.getAuthor().getAsMention() + " Please don't use blacklisted words!"
                ).queue();
            }
        }
    }

}
