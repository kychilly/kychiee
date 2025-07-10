package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.BlacklistCommand;
import com.kychilly.DiscordBot.utils.BlacklistManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BlacklistedWordsListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getGuild() != null && event.getMessage().getContentRaw() != null) {
            String message = event.getMessage().getContentRaw();
            if (BlacklistManager.containsBlacklistedWord(event.getGuild(), message)) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(
                        event.getAuthor().getAsMention() + " Please don't use blacklisted words!"
                ).queue();
            }
        }
    }

}
