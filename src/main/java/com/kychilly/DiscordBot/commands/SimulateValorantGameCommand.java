package com.kychilly.DiscordBot.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.kychilly.DiscordBot.classes.ValorantGame;

import java.util.*;

public class SimulateValorantGameCommand {

    public static Map<String, ValorantGame> activeGames = new HashMap<>();

    public static String ran = "";

    static Random rand = new Random();

    static String[] maps = {
            "Ascent","Bind","Haven","Split","Lotus","Icebox","Breeze","Sunset"
    };

    static String[] duelists = {"Jett","Reyna","Raze","Neon","Yoru","Phoenix","Iso"};
    static String[] initiators = {"Sova","Skye","Fade","Breach","Gekko","KAY/O"};
    static String[] controllers = {"Brimstone","Omen","Viper","Harbor","Astra","Clove"};
    static String[] sentinels = {"Cypher","Killjoy","Sage","Chamber","Deadlock"};

    static String[] allAgents = {
            "Jett","Reyna","Raze","Neon","Yoru","Phoenix","Iso",
            "Sova","Skye","Fade","Breach","Gekko","KAY/O",
            "Brimstone","Omen","Viper","Harbor","Astra","Clove",
            "Cypher","Killjoy","Sage","Chamber","Deadlock"
    };

    public static CommandData getCommandData() {
        return Commands.slash("simulatevalorantgame", "Play a Valorant match simulator");
    }

    public static void execute(SlashCommandInteractionEvent event) {

        int random = rand.nextInt(maps.length);
        String map = maps[random];
        ran = map;

        String teamAAgents = generateTeam();
        String teamBAgents = generateTeam();

        ValorantGame game = new ValorantGame(map);
        game.teamAAgents = teamAAgents;
        game.teamBAgents = teamBAgents;

        activeGames.put(event.getUser().getId(), game);

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Valorant Match Started");
        embed.setThumbnail(getMapThumbnail(map));
        embed.setDescription(
                "Map: **" + map + "**\n\n" +

                        "**Team A**\n" +
                        teamAAgents + "\n\n" +

                        "**Team B**\n" +
                        teamBAgents + "\n\n" +

                        "Score: **0-0**\n" +
                        "Money: **800**\n\n" +

                        "Round 1\nChoose your buy."
        );

        event.replyEmbeds(embed.build())
                .addActionRow(
                        Button.primary("valorant_eco", "Eco (800)"),
                        Button.secondary("valorant_half", "Half Buy (2500)"),
                        Button.danger("valorant_full", "Full Buy (4000)")
                ).queue();
    }

    static String random(String[] arr) {
        return arr[rand.nextInt(arr.length)];
    }

    static String randomWithoutDuplicate(String[] pool, Set<String> used) {

        String agent;

        do {
            agent = random(pool);
        } while(used.contains(agent));

        used.add(agent);
        return agent;
    }

    static String randomFlex(Set<String> used) {

        String agent;

        do {
            agent = random(allAgents);
        } while(used.contains(agent));

        used.add(agent);
        return agent;
    }

    static String generateTeam() {

        Set<String> used = new HashSet<>();

        String duelist = randomWithoutDuplicate(duelists, used);
        String initiator = randomWithoutDuplicate(initiators, used);
        String controller = randomWithoutDuplicate(controllers, used);
        String sentinel = randomWithoutDuplicate(sentinels, used);
        String flex = randomFlex(used);

        return "**Duelist:** " + getAgentPicture(duelist) + "\n" +
                "**Initiator:** " + getAgentPicture(initiator) + "\n" +
                "**Controller:** " + getAgentPicture(controller) + "\n" +
                "**Sentinel:** " + getAgentPicture(sentinel) + "\n" +
                "**Flex:** " + getAgentPicture(flex);
    }


    static String getAgentPicture(String agent) {
        return agent;
    } // bruh
//    static String getAgentPicture(String agent) {
//
//        switch (agent.toLowerCase()) {
//
//            case "deadlock":
//                return "<:deadlocked:1480257509162106210>";
//
//            case "chamber":
//                return "<:chamber:1480257694305935410>";
//
//            case "sage":
//                return "<:sage:1480257682658353193>";
//
//            case "killjoy":
//                return "<:killjoy:1480257668284739846>";
//
//            case "cypher":
//                return "<:cypher:1480257652505636994>";
//
//            case "clove":
//                return "<:clove:1480257625167159598>";
//
//            case "viper":
//                return "<:viper:1480257581135224922>";
//
//            case "omen":
//                return "<:omen:1480257559819907312>";
//
//            case "brimstone":
//            case "brim":
//                return "<:brim:1480257547023212667>";
//
//            case "kayo":
//            case "kay/o":
//                return "<:kayo:1480257492622831739>";
//
//            case "gekko":
//                return "<:gekko:1480257476198195272>";
//
//            case "breach":
//                return "<:breach:1480257451535565628>";
//
//            case "fade":
//                return "<:fade:1480257436331073659>";
//
//            case "skye":
//                return "<:skye:1480257429102934168>";
//
//            case "sova":
//                return "<:sova:1480257408719974722>";
//
//            case "iso":
//                return "<:iso:1480257393545113631>";
//
//            case "phoenix":
//                return "<:phoenix:1480257380567601016>";
//
//            case "yoru":
//                return "<:yoru:1480257363736322130>";
//
//            case "neon":
//                return "<:neon:1480257346686356468>";
//
//            case "raze":
//                return "<:raze:1480257329460338759>";
//
//            case "reyna":
//                return "<:reyna:1480257309415628916>";
//
//            case "jett":
//                return "<:jett:1480257284237222071>";
//        }
//
//        return "";
//    }

    static String getMapThumbnail(String map) {

        switch (map) {
            case "Ascent":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479997793740390482/2Q.png?ex=69ae12db&is=69acc15b&hm=21d5467d907a78212398ea570b2c7ff6ede0df504bd4695e1939f23ac11f3c32&=&format=webp&quality=lossless&width=225&height=126";

            case "Bind":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479997879354392708/9k.png?ex=69ae12ef&is=69acc16f&hm=608ef0edd7041bb3d654f7bd846a47fe520690e678a206d35309394b92b57024&=&format=webp&quality=lossless&width=225&height=126";

            case "Haven":
                return "https://images-ext-1.discordapp.net/external/pZnou83f8-RCEF-iUWGKpXEgOV13mQja5bZGgUQPoQE/%3Fcb%3D20200620202335/https/static.wikia.nocookie.net/valorant/images/7/70/Loading_Screen_Haven.png/revision/latest?format=webp&width=1440&height=810";

            case "Split":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479998108623573186/2Q.png?ex=69ae1326&is=69acc1a6&hm=4a05ff0f855183d185f3941d72baca0f7694f944d3b36d7442687ccd8b6344db&=&format=webp&quality=lossless&width=225&height=126";

            case "Lotus":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479998194539561060/latest.png?ex=69ae133a&is=69acc1ba&hm=640dd6cc9312b94bff0b354dcf9678c92ed5bfa68aea28d9eb28ffce1c5011c4&=&format=webp&quality=lossless&width=1440&height=810";

            case "Icebox":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479998286365720676/2Q.png?ex=69ae1350&is=69acc1d0&hm=586c26dadbae4c7c620d9d10f00ddf5f38248cdc276d3b7cdcba2af2ae2289e0&=&format=webp&quality=lossless&width=225&height=126";

            case "Breeze":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479998365713567744/latest.png?ex=69ae1363&is=69acc1e3&hm=a94d8a01f8cd4e275bff1e3270f76213a56ca8d18ce523ac454a01da2291a10a&=&format=webp&quality=lossless&width=1512&height=851";

            case "Sunset":
                return "https://media.discordapp.net/attachments/1186115783013711894/1479998453458141494/9k.png?ex=69ae1378&is=69acc1f8&hm=5663ec5a06a186e0519ee6422ee1d4f9298cd017275d779c4e6e37f32384fbc4&=&format=webp&quality=lossless&width=225&height=126";
        }

        return null;
    }

}