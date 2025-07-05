package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class DeleteChannelCommand extends ListenerAdapter {

    public static CommandData getCommandData() {
        return Commands.slash("deletechannel", "Deletes a text channel by name")
                .addOption(OptionType.STRING, "name", "Name of the channel to delete", true);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getIdLong() != 840216337119969301L) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You do not have perms lol").setEphemeral(true).queue();
                return;
            }
        }

        String channelName = event.getOption("name").getAsString();

        GuildMessageChannel channelToDelete = event.getGuild()
                .getChannels()
                .stream()
                .filter(c -> c instanceof GuildMessageChannel)
                .map(c -> (GuildMessageChannel) c)
                .filter(c -> c.getName().equalsIgnoreCase(channelName))
                .findFirst()
                .orElse(null);

        if (channelToDelete == null) {
            event.reply("❌ Could not find a text-like message channel with the name **" + channelName + "**").setEphemeral(true).queue();
            return;
        }

        channelToDelete.delete()
                .reason("Deleted via slash command")
                .queue(
                        success -> event.reply("✅ Channel **" + channelName + "** deleted.").setEphemeral(false).queue(),
                        error -> event.reply("❌ Failed to delete channel: " + error.getMessage()).setEphemeral(true).queue()
                );
    }
}
