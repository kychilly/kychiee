package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.utils.UsersManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class UsersCommand {

    private static final int USERS_PER_PAGE = 10;

    public static CommandData getCommandData() {
        return Commands.slash("addusers", "adds users to databook")
                .addOption(OptionType.INTEGER, "users", "amount of users to add", true);
    }



    public static void execute(SlashCommandInteractionEvent event) {

        if (Objects.requireNonNull(event.getMember()).getIdLong() != 840216337119969301L) {
            event.reply("lol sorry you are not kyche").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue(); // Prevent timeout

        int times = event.getOption("users").getAsInt();
        Guild guild = event.getGuild();

        if (guild == null) {
            event.getHook().sendMessage("Guild not found.").queue();
            return;
        }

        // Run heavy work async (DO NOT block JDA thread)
        CompletableFuture.runAsync(() -> {
            try {

                int totalToAdd = times;

                // Load users once
                Set<Long> users = UsersManager.getUserList(guild);

                for (int i = 0; i < totalToAdd; i++) {
                    users.add(getRandomLong());
                }

                // Save once
                UsersManager.saveUsers();

                event.getHook()
                        .sendMessage("" + totalToAdd + " skibideez found!")
                        .setEphemeral(true)
                        .queue();

            } catch (Exception e) {
                e.printStackTrace();
                event.getHook()
                        .sendMessage("Something went wrong.")
                        .queue();
            }
        });
    }


    // Returns a random long
    public static long getRandomLong() {
        long min = 100_000_000_000_000_000L;  // smallest 18-digit number
        long max = 999_999_999_999_999_999L;  // largest 18-digit number
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

}
