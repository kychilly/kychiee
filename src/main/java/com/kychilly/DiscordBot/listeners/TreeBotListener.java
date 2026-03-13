package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class TreeBotListener extends ListenerAdapter {

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.getMember().getUser().getIdLong() == 972637072991068220L) {
            if (event.getMessage().getContentDisplay().contains("Ready to be watered!")) {
                TextChannel channel = event.getGuild().getTextChannelById(840216337119969301L);

                channel.sendMessage("<@840216337119969301>, reminder to water the tree!").queue();
            }
        }
    }
}
