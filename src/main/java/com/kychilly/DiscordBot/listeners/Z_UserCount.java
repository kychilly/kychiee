package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.utils.UsersManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Z_UserCount extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.equals("!users") || message.equals("!user")) {
            JDA jda = event.getJDA();
            Guild guild = jda.getGuildById(1186115782313267321L);
            event.getChannel().sendMessage("" + UsersManager.getUserAmount(guild)).queue();
        }
    }
}
