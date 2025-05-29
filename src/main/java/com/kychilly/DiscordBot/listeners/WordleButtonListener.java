package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.WordleCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class WordleButtonListener extends ListenerAdapter {

    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();

        if (buttonId.startsWith("wordle:")) {
            String action = buttonId.split(":")[1];
            User user = event.getUser();

            switch (action) {
                case "quit":
                    WordleCommand.handleQuit(user, event);
                    break;
                case "help":
                    WordleCommand.handleHelp(user, event);
                    break;
            }
        }
    }

}
