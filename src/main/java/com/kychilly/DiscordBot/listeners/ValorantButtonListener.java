package com.kychilly.DiscordBot.listeners;

import com.kychilly.DiscordBot.commands.SimulateValorantGameCommand;
import com.kychilly.DiscordBot.classes.ValorantGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

import static com.kychilly.DiscordBot.commands.SimulateValorantGameCommand.ran;

public class ValorantButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        String id = event.getComponentId();

        if(!id.startsWith("valorant_")) return;

        String userId = event.getUser().getId();

        ValorantGame game =
                SimulateValorantGameCommand.activeGames.get(userId);

        if(game == null){
            event.reply("No active Valorant game.").setEphemeral(true).queue();
            return;
        }

        String choice = id.replace("valorant_", "");

        int cost;
        double playerChance;

        switch (choice){
            case "full":
                cost = 4000;
                playerChance = 0.67;
                break;

            case "half":
                cost = 2500;
                playerChance = 0.40;
                break;

            default:
                cost = 800;
                playerChance = 0.25;
        }

        game.teamAMoney -= cost;

        String enemyBuy;
        int enemyCost;
        double enemyChance;

        if(game.teamBMoney >= 4000){
            enemyBuy = "Full Buy";
            enemyCost = 4000;
            enemyChance = 0.67;
        }
        else if(game.teamBMoney >= 2500){
            enemyBuy = "Half Buy";
            enemyCost = 2500;
            enemyChance = 0.40;
        }
        else{
            enemyBuy = "Eco";
            enemyCost = 800;
            enemyChance = 0.25;
        }

        game.teamBMoney -= enemyCost;

        double finalChance = playerChance / (playerChance + enemyChance);

        boolean playerWins = Math.random() < finalChance;

        int teamASurvivors;
        int teamBSurvivors;

        if(playerWins){
            teamASurvivors = (int)(Math.random() * 5) + 1; // 1-5 survive
            teamBSurvivors = (int)(Math.random() * 2); // usually 0-1 survive when losing
        }
        else{
            teamBSurvivors = (int)(Math.random() * 5) + 1;
            teamASurvivors = (int)(Math.random() * 2);
        }

        int savedA = teamASurvivors * 500; // I dont like this at all, suspicious but ok
        int savedB = teamBSurvivors * 500;

        game.teamAMoney += savedA;
        game.teamBMoney += savedB;

        if(playerWins){
            game.teamAScore++;
            game.teamAMoney += 3000;
            game.teamBMoney += 1900;
        }
        else{
            game.teamBScore++;
            game.teamBMoney += 3000;
            game.teamAMoney += 1900;
        }

        if (game.teamAMoney > 9000) { // caps money
            game.teamAMoney = 9000;
        }
        if (game.teamBMoney > 9000) {
            game.teamBMoney = 9000;
        }

        game.round++;

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Round Result");

        embed = playerWins ? embed.setColor(Color.GREEN) : embed.setColor(Color.RED);

        embed.setThumbnail(getMapThumbnail(ran));

        embed.setDescription(
                        "**Team A**\n" +
                        game.teamAAgents + "\n\n" +

                        "**Team B**\n" +
                        game.teamBAgents + "\n\n" +

                        "You chose: **" + choice.toUpperCase() + "**\n" +
                        "Enemy buy: **" + enemyBuy + "**\n\n" +

                        (playerWins ? "**YOU WON THE ROUND!**" : "**Enemy won the round.**") +

                                "\n\nSurvivors:\n" +
                                "Team A: **" + teamASurvivors + "/5**\n" +
                                "Team B: **" + teamBSurvivors + "/5**\n\n" +

                        "Score: **" + game.teamAScore + "-" + game.teamBScore + "**\n" +
                        "Your Money: **" + game.teamAMoney + "**\n" +
                        "Enemy Money: **" + game.teamBMoney + "**"
        );

        if(game.isOver()){

            SimulateValorantGameCommand.activeGames.remove(userId);

            embed.setFooter(
                    game.teamAScore > game.teamBScore ?
                            "You won the match!" :
                            "Enemy won the match!"
            );

            event.editMessageEmbeds(embed.build())
                    .setComponents()
                    .queue();

            return;
        }

        event.editMessageEmbeds(embed.build())
                .setActionRow(
                        Button.primary("valorant_eco", "Eco (800)"),
                        Button.secondary("valorant_half", "Half Buy (2500)"),
                        Button.danger("valorant_full", "Full Buy (4000)")
                ).queue();
    }

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