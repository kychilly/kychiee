package com.kychilly.DiscordBot.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TyperacerCommand {

    String raceText = "";
    ArrayList<String> list = new ArrayList<>();

    public ArrayList<String> decodeJSON(String filePath) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();

        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(WordBomb.class.getClassLoader().getResourceAsStream("com/github/KychillyBot/wordbomb/dictionary.txt.json")));
        {
            return gson.fromJson(reader, listType);
        }
    }


}
