package com.kychilly.DiscordBot.classes;

public class ValorantGame {

    public int teamAScore = 0;
    public int teamBScore = 0;

    public int teamAMoney = 800;
    public int teamBMoney = 800;

    public String teamAAgents;
    public String teamBAgents;

    public int round = 1;

    public String map;

    public ValorantGame(String map) {
        this.map = map;
    }

    public boolean isOver() {
        return teamAScore >= 13 || teamBScore >= 13;
    }
}
