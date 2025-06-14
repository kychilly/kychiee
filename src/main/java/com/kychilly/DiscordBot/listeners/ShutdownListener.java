package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ShutdownListener extends ListenerAdapter {
    private static final long CHANNEL_ID = 1186115783013711894L;

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            TextChannel channel = event.getJDA().getTextChannelById(CHANNEL_ID);
            if (channel == null) {
                System.out.println("Shutdown channel not found!");
                return;
            }

            channel.sendMessage("Bot is shutting down. Bye byes!").queue(
                    success -> System.out.println("Sent shutdown message"),
                    error -> System.err.println("Failed to send shutdown message: " + error)
            );
        } catch (Exception e) {
            System.err.println("Error in shutdown handler: " + e);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        TextChannel channel = event.getJDA().getTextChannelById(CHANNEL_ID);
        channel.sendMessage("your stupid listener is online").queue();
    }
}