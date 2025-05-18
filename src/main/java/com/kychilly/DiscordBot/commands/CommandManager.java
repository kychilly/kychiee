package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

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
            //do typeracer lol
        } else if (command.equals("emily")) {
            event.reply("<:emily:1327147566311407679>").queue();
        } else if (command.equals("peashooter")) {
            event.reply("<:peashooter:1363400493967343757>").queue();
        } else if (command.equals("remind")) {
            HandleReminderCommand.execute(event);
        } else if (command.equals("ban")) {
            BanCommand.execute(event);
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "welcomes user"));
        commandData.add(Commands.slash("roles", "gets all roles on discord server"));
        commandData.add(Commands.slash("emily", ":emily:"));
        commandData.add(Commands.slash("peashooter", "peashooter image"));
        commandData.add(HandleReminderCommand.getCommandData());
        commandData.add(BanCommand.getCommandData());
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }



    //if want to have these commands on other guilds, do onGuildJoin, copy paste everything from onGuildReady here
}
