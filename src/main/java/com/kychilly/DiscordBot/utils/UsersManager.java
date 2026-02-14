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
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<Long, Set<Long>>>(){}.getType();
            guildUsers = gson.fromJson(reader, type);
            if (guildUsers == null) {
                guildUsers = new HashMap<>();
            }
        } catch (IOException e) {
            System.out.println("No existing users file found, creating new one.");
            guildUsers = new HashMap<>();
        }
    }

    private static void saveUsers() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(guildUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<Long> getUserList(Guild guild) {
        return guildUsers.computeIfAbsent(guild.getIdLong(), k -> new HashSet<>());
    }

    public static void addUser(Guild guild, long user) {
        Set<Long> userList = getUserList(guild);
        userList.add(user);
        saveUsers();
    }



}
