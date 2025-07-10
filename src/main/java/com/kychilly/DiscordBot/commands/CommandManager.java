package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
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
                new WordBomb().execute(event); // for some reason needs a thrown exception
            } catch (IOException e) {
                event.reply("An error occurred while starting WordBomb: " + e.getMessage()).setEphemeral(true).queue();
            }
        } else if (command.equals("help")) {
            event.reply("DM user \"<@840216337119969301>\" for temporary assitance. Additionally, you can join the support server for more help: https://discord.gg/WsnJEutfbd").setEphemeral(true).queue();
        } else if (command.equals("kycheintro")) {
            event.reply("Kyche's intro stuff: https://docs.google.com/document/d/1OOTuTdukwk9Sbr30bHp-9rkINbiVHhsLgih62inuG3E/edit?tab=t.0").setEphemeral(true).queue();
        } else if (command.equalsIgnoreCase("kycheGithub")) {
            event.reply("Kyche's amazing github: https://github.com/kychilly/kychiee").queue();
        } else if (command.equalsIgnoreCase("shutdown")) {
            ShutdownCommand.execute(event);
        } else if (command.equals("change-nickname")) {
            ChangeNicknameCommand.execute(event);
        } else if (command.equals("channel")) {
            TextChannelCommand.execute(event);
        } else if (command.equals("deletechannel")) {
            DeleteChannelCommand.execute(event);
        } else if (command.equals("minesweeper")) {
            MinesweeperCommand.execute(event);
        } else if (command.equals("timer")) {
            TimerCommand.execute(event);
        } else if (command.equals("roll")) {
            RollCommand.execute(event);
        } else if (command.equals("skibidi")) {
            SkibidiCommand.execute(event);
        } else if (command.equals("selfpromo")) {
            SelfPromoCommand.execute(event);
        } else if (command.equals("sigma_roulette")) {
            RouletteCommand.execute(event);
        } else if (command.equals("creator")) {
            event.reply("Discord: <@840216337119969301>\nWebsite: [customdiscordbots.com](https://customdiscordbots.com)").queue();
        } else if (command.equals("blacklist")) {
            BlacklistCommand.execute(event);
        } else if (command.equals("removeblacklist")) {
            RemoveBlacklistCommand.execute(event);
        } else if (command.equals("viewblacklist")) {
            ViewBlacklistCommand.execute(event);
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        //filler commands
        commandData.add(Commands.slash("welcome", "welcomes user"));
        commandData.add(Commands.slash("roles", "gets all roles on discord server"));
        commandData.add(Commands.slash("peashooter", "peashooter image"));
        commandData.add(Commands.slash("help", "Gives additional help"));
        commandData.add(SelfPromoCommand.getCommandData());
        commandData.add(SkibidiCommand.getCommandData());
        commandData.add(PfpCommand.getCommandData());
        commandData.add(HandleReminderCommand.getCommandData());
        commandData.add(TimerCommand.getCommandData());
        commandData.add(RollCommand.getCommandData());
        commandData.add(Commands.slash("creator", "gets bot info"));

        //moderation commands
        commandData.add(BanCommand.getCommandData());
        commandData.add(KickCommand.getCommandData());
        commandData.add(TimeoutCommand.getCommandData());
        commandData.add(ChangeNicknameCommand.getCommandData());
        commandData.add(BlacklistCommand.getCommandData());
        commandData.add(RemoveBlacklistCommand.getCommandData());
        commandData.add(ViewBlacklistCommand.getCommandData());

        //game commands
        commandData.add(WordBomb.getCommandData());
        commandData.add(WordleCommand.getCommandData());
        commandData.add(Commands.slash("guess", "Make a guess in your Wordle game")
                .addOption(OptionType.STRING, "word", "Your 5-letter guess", true));

        commandData.add(Commands.slash("typeracer", "Play typeracer!!"));
        commandData.add(MinesweeperCommand.getCommandData());
        commandData.add(RouletteCommand.getCommandData());

        //kyche commands
        commandData.add(Commands.slash("kyche", "kyche's intro :D"));
        commandData.add(Commands.slash("kychegithub", "kyche's amazing github"));


        //bot commands
        commandData.add(ShutdownCommand.getCommandData());
        commandData.add(TextChannelCommand.getCommandData());
        commandData.add(DeleteChannelCommand.getCommandData());



        //updates all commands in guilds
        event.getGuild().updateCommands()
                .addCommands(commandData)
                .queue(
                        success -> System.out.println("✅ Commands registered in " + event.getGuild().getName()),
                        error -> System.err.println("❌ Failed in " + event.getGuild().getName() + ": " + error.getMessage())
                );

    }



    //if want to have these commands on other guilds, do onGuildJoin, copy paste everything from onGuildReady here
}
