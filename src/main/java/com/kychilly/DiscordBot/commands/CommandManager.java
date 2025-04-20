package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;



import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equalsIgnoreCase("welcome")) {
            String userTag = event.getUser().getAsMention();
            event.reply("Welcome, **" + userTag + "**!").queue();
        }//PLEASE REMEMBER TO REMOVE THIS LATER
        else if (command.equalsIgnoreCase("kys")) {
            event.deferReply().queue();//if takes longer than 3seconds for command to respond
            event.getHook().sendMessage("KILL YOURSELF NOW! :robot::joy:").queue();
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
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "welcomes user"));
        commandData.add(Commands.slash("kys", "PLEASE REMEMBER TO DELETE THIS LATER"));
        commandData.add(Commands.slash("roles", "gets all roles on discord server"));
        commandData.add(Commands.slash("emily", ":emily:"));
        commandData.add(Commands.slash("peashooter", "peashooter image"));
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    //if want to have these commands on other guilds, do onGuildJoin, copy paste everything from onGuildReady here

}
