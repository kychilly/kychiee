package com.kychilly.DiscordBot.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsersManager {

    private static final String FILE_PATH = "users.json";
    private static final Gson gson = new Gson();
    private static Map<Long, Set<Long>> guildUsers = new HashMap<>();

    static {
        loadUsers();
    }

    private static void loadUsers() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                guildUsers = new HashMap<>();
                return;
            }

            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<Long, Set<Long>>>() {}.getType();
                Map<Long, Set<Long>> loaded = gson.fromJson(reader, type);

                guildUsers = (loaded != null) ? loaded : new HashMap<>();
            }

        } catch (Exception e) {  // catch EVERYTHING
            System.out.println("Users file invalid. Resetting.");
            guildUsers = new HashMap<>();
        }
    }


    public static void saveUsers() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(guildUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<Long> getUserList(Guild guild) {
        return guildUsers.computeIfAbsent(guild.getIdLong(), k -> new HashSet<>());
    }

    public static int getUserAmount(Guild guild) {
        return UsersManager.getUserList(guild).size();
    }

    public static void addUser(Guild guild, long user) {
        Set<Long> userList = getUserList(guild);
        userList.add(user);
        saveUsers();
    }



}
