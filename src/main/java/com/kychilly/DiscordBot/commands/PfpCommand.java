package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class PfpCommand {

    // Command registration data
    public static List<OptionData> getOptions() {
        return Collections.singletonList(
                new OptionData(OptionType.USER, "user", "The user whose pfp you want", false)
        );
    }

    public static CommandData getCommandData() {
        return (Commands.slash("pfp", "Get a user's profile picture")
                .addOption(OptionType.USER, "user", "The user whose pfp you want", false));
    }

    // Command execution
    public static EmbedBuilder execute(SlashCommandInteractionEvent event) {
        // Get target user (either specified user or command user)
        User target = event.getOption("user", event.getUser(), OptionMapping::getAsUser);

        // Create embed with high-resolution avatar
        return new EmbedBuilder()
                .setTitle(target.getName() + "'s Profile Picture")
                .setImage(target.getEffectiveAvatarUrl() + "?size=4096") // Max resolution
                .setColor(Color.CYAN)
                .setFooter("Requested by " + event.getUser().getName(),
                        event.getUser().getEffectiveAvatarUrl());
    }
}