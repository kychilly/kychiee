package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equalsIgnoreCase("welcome")) {
            String userTag = event.getUser().getAsMention();
            event.reply("Welcome, **" + userTag + "**!").queue();
        } else if (command.equals("roles")) {
            String response = "";
            for (Role roles : event.getGuild().getRoles()) {
                response += roles.getAsMention() + "\n";
            }
            event.reply(response).setEphemeral(true).queue();
        } else if (command.equals("typeracer")) {
            new TyperacerCommand(event);
        } else if (command.equals("peashooter")) {
            event.reply("<:peashooter:1363400493967343757>").queue();
        } else if (command.equals("remind")) {
            HandleReminderCommand.execute(event);
        } else if (command.equals("ban")) {
            BanCommand.execute(event);
        } else if (command.equals("kick")) {
            KickCommand.execute(event);
        } else if (command.equals("pfp")) {
            event.replyEmbeds(PfpCommand.execute(event).build()).queue();
        } else if (command.equals("timeout")) {
            TimeoutCommand.handleCommand(event);
        } else if (command.equals("wordle")) {
            WordleCommand wordleCommand = new WordleCommand();
            wordleCommand.handleCommand(event);
        } else if (command.equals("guess")) {
            WordleCommand.handleGuess(event);
        } else if (command.equals("wordbomb")) {
            try {
                new WordBomb().execute(event); // instantiate and run
            } catch (IOException e) {
                event.reply("An error occurred while starting WordBomb: " + e.getMessage()).setEphemeral(true).queue();
            }
        } else if (command.equals("help")) {
            event.reply("DM user \"<@840216337119969301>\" for temporary assitance. Additionally, you can join the support server for more help: https://discord.gg/WsnJEutfbd").setEphemeral(true).queue();
        } else if (command.equals("kyche")) {
            event.reply("Kyche's intro stuff: https://docs.google.com/document/d/1OOTuTdukwk9Sbr30bHp-9rkINbiVHhsLgih62inuG3E/edit?tab=t.0").setEphemeral(true).queue();
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "welcomes user"));
        commandData.add(Commands.slash("roles", "gets all roles on discord server"));
        commandData.add(Commands.slash("peashooter", "peashooter image"));
        commandData.add(Commands.slash("help", "Gives additional help"));
        commandData.add(HandleReminderCommand.getCommandData());
        commandData.add(BanCommand.getCommandData());
        commandData.add(KickCommand.getCommandData());



        commandData.add(Commands.slash("pfp", "Get a user's profile picture")
                .addOptions(PfpCommand.getOptions()));

        commandData.add(Commands.slash("wordbomb", "Play WordBomb on Discord!")
                .addOptions(
                        (new OptionData(OptionType.INTEGER, "difficulty", "Difficulty of the letter sequences given")
                                .addChoice("Easy", 1)
                                .addChoice("Medium", 2)
                                .addChoice("Hard", 3)
                                .setRequired(true)
                        ),
                        (new OptionData(OptionType.INTEGER, "language", "Language to play WordBomb in (default: English)")
                                .addChoice("English", 0)
                                .addChoice("Spanish", 1)
                                .setRequired(false)
                        )
                )
        );

        commandData.add(WordleCommand.getCommandData());
        commandData.add(Commands.slash("guess", "Make a guess in your Wordle game")
                .addOption(OptionType.STRING, "word", "Your 5-letter guess", true));
        commandData.add(Commands.slash("typeracer", "Play typeracer!!"));

        commandData.add(TimeoutCommand.getCommandData());

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }



    //if want to have these commands on other guilds, do onGuildJoin, copy paste everything from onGuildReady here
}
