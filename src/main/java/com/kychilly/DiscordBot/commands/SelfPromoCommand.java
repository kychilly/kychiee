package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;

public class SelfPromoCommand {

    public static CommandData getCommandData() {
        return Commands.slash("selfpromo", "Who doesn't love a little self promo?");
    }

    public static void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDEA8 PLEASE READ BELOW PLS PLS PLS \uD83D\uDEA8")
                .setThumbnail("https://media.discordapp.net/attachments/1186115783013711894/1389440152752881744/ysgaCm4fSKMAAAAASUVORK5CYII.png?ex=6864a07a&is=68634efa&hm=e7a7907921e062abadc726b217814c9166cf30d50a31b5fb753bf9f1909f4123&=&format=webp&quality=lossless&width=454&height=283")//crying image lol
                .setDescription("Please buy personal discord bot services :pray::pray::pray::pray::pray::pray:")
                .setColor(Color.GREEN)
                .addField("Contact \uD83D\uDDE3\uFE0F",
                        "[jyam478@gmail.com](mailto:jyam478@gmail.com)  \n[woodlandmansion.com](https://woodlandmansion.com) \n [github.com/kychilly](https://github.com/kychilly)",
                        false)
                .setFooter("PLEAEAAEAASSSESEEE I NEED MONEY");
        event.replyEmbeds(embed.build()).queue();
    }

}
