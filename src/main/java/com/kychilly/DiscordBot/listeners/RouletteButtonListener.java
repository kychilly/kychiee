package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.RouletteCommand;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class RouletteButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        RouletteCommand.handleButton(event);
    }
}