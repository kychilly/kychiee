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

    public static List<CommandData> commandData = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("roles")) {
            String response = "";
            for (Role roles : event.getGuild().getRoles()) {
                response += roles.getAsMention() + "\n";
            }
            event.reply(response).setEphemeral(true).queue();
        } else if (command.equals("typeracer")) {
            new TyperacerCommand(event);
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
        } else if (command.equals("commands")) {
            CommandsListCommand.execute(event);
        } else if (command.equals("send-message")) {
            TextChannelSendMessageCommand.execute(event);
        } else if (command.equals("ping-user")) {
            Z_PingingUserCommand.execute(event);
        } else if (command.equals("dm-user")) {
            DM_Command.execute(event);
        } else if (command.equals("sixseven")) {
            SixSevenCommand.execute(event);
        }
    }

    public static void initializeCommands() {

        //filler commands
        commandData.add(Commands.slash("roles", "gets all roles on discord server"));
        commandData.add(SelfPromoCommand.getCommandData());
        commandData.add(SkibidiCommand.getCommandData());
        commandData.add(PfpCommand.getCommandData());
        commandData.add(HandleReminderCommand.getCommandData());
        commandData.add(com.kychilly.DiscordBot.commands.TimerCommand.getCommandData());
        commandData.add(RollCommand.getCommandData());
        commandData.add(Commands.slash("creator", "gets bot info"));
        commandData.add(Commands.slash("commands", "Gets a list of all possible commands"));
        commandData.add(Z_PingingUserCommand.getCommandData());
        commandData.add(DM_Command.getCommandData());
        commandData.add(SixSevenCommand.getCommandData());

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


        //bot commands
        commandData.add(ShutdownCommand.getCommandData());
        commandData.add(TextChannelCommand.getCommandData());
        commandData.add(DeleteChannelCommand.getCommandData());
        commandData.add(TextChannelSendMessageCommand.getCommandData());
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {



        //updates all commands in guilds
        event.getGuild().updateCommands()
                .addCommands(commandData)
                .queue(
                        success -> System.out.println("✅ Commands registered in " + event.getGuild().getName()),
                        error -> System.err.println("❌ Failed in " + event.getGuild().getName() + ": " + error.getMessage())
                );

    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {

        //updates all commands in guilds
        event.getGuild().updateCommands()
                .addCommands(commandData)
                .queue(
                        success -> System.out.println("✅ New commands registered in " + event.getGuild().getName()),
                        error -> System.err.println("❌ Failed in " + event.getGuild().getName() + ": " + error.getMessage())
                );

    }

    //if want to have these commands on other guilds, do onGuildJoin, copy paste everything from onGuildReady here
}
