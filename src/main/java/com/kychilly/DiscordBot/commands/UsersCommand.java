package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.utils.UsersManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class UsersCommand {

    private static final int USERS_PER_PAGE = 10;

    public static CommandData getCommandData() {
        return Commands.slash("addusers", "adds users to databook")
                .addOption(OptionType.INTEGER, "users", "amount of users to add(each iteration is about 450 users)", true);
    }



    public static void execute(SlashCommandInteractionEvent event) {
        if (Objects.requireNonNull(event.getMember()).getIdLong() != 840216337119969301L) {
            event.reply("lol sorry you are not kyche").setEphemeral(true).queue();
            return;
        }

        int times = event.getOption("users").getAsInt();

// do the thing times times
        int totalTextChannels = event.getGuild().getTextChannels().size();
        for (int p = 0; p < times; p++) {
            for (int i = 4; i < totalTextChannels-1; i++) {
                try {
                    Long randomLong = getRandomLong();
                    UsersManager.addUser(event.getGuild(), randomLong);
                    event.getChannel().sendMessage("added a random user: <@" + randomLong + ">").queue();
                    Thread.sleep(25);
                } catch (Exception e) {
                    System.out.println("I AM NOT HAPPY");
                    event.getChannel().sendMessage("Oops! Something happened. Stopping command, please fix kyche.").queue();
                    break;
                }
            }
        }

    }

    // Returns a random long
    public static long getRandomLong() {
        long min = 100_000_000_000_000_000L;  // smallest 18-digit number
        long max = 999_999_999_999_999_999L;  // largest 18-digit number
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

}
