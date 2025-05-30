package com.kychilly.DiscordBot.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kychilly.DiscordBot.classes.TyperacerPlayer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TyperacerCommand {

    String raceText = "";
    static HashMap<Long, TyperacerPlayer> typeracerGames = new HashMap<>();

    public TyperacerCommand(SlashCommandInteractionEvent event) {
        if (typeracerGames.get(event.getChannelIdLong()) != null)
            event.reply("There is an ongoing typeracer game going on in the same channel").queue();
        typeracerGames.put(event.getChannelIdLong(), new TyperacerPlayer(event));
    }

    public static boolean PlayingTyperacer(long channelID) {
        if (typeracerGames.get(channelID) != null) return true;
        return false;
    }

    public static HashMap<Long, TyperacerPlayer> getTyperacerGames() {
        return typeracerGames;
    }

}
