package com.kychilly.DiscordBot.classes;

import java.util.HashMap;
import java.util.Map;

public class MinesweeperGameHandler {
    private final Map<String, MinesweeperGame> activeGames = new HashMap<>();

    public void createGame(String userId, int width, int height, int bombCount) {
        activeGames.put(userId, new MinesweeperGame(width, height, bombCount));
    }

    public MinesweeperGame getGame(String userId) {
        return activeGames.get(userId);
    }

    public boolean hasActiveGame(String userId) {
        return activeGames.containsKey(userId);
    }

    public void endGame(String userId) {
        activeGames.remove(userId);
    }


}
