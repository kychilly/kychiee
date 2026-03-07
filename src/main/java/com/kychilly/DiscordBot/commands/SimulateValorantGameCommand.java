package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class SimulateValorantGameCommand {

    public static CommandData getCommandData() {
        return Commands.slash("simulatevalorantgame", "Simulates a valorant game(WIP)");
    }

    public static void execute(SlashCommandInteractionEvent event) {
        int a = 0;
        int b = 0;
        while (a < 13 && b < 13) {
            if (Math.random() > .5) a++; else b++;
        }
        String w = String.format("A wins %d-%d", a,b);
        if (b >= 13) w = String.format("B wins %d-%d",b,a);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail("https://media.discordapp.net/attachments/1186115783013711894/1479991070812278888/9k.png?ex=69ae0c98&is=69acbb18&hm=2b04c7efc1508d95eb15f798823787bad449ce966051403d7fc44ce0f50fa176&=&format=webp&quality=lossless&width=169&height=169");
        embed.setTitle(String.format("Team %s", w));
        embed.setFooter(":D");
        event.replyEmbeds(embed.build()).queue();
    }

}
