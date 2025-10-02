package com.kychilly.DiscordBot.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class DM_Command {

    public static CommandData getCommandData() {
        return Commands.slash("dm-user", "dm's a user a set amount of times")
                .addOption(OptionType.USER, "user", "the user you want to dm", true)
                .addOption(OptionType.STRING, "message", "the message you want to send", true)
                .addOption(OptionType.INTEGER, "times", "how many iterations of messages to send", false);
    }

    public static void execute(SlashCommandInteractionEvent event) {
        User user = event.getOption("user").getAsUser();
        String message = event.getOption("message").getAsString();
        int times = event.getOption("times") == null ? 1 : event.getOption("times").getAsInt();

        if (times > 100) {
            event.reply("Please use a number within 1-100 D:").setEphemeral(true).queue();
            return;
        }

        event.reply("yayyy ok").queue();


        event.getGuild().retrieveMember(user).queue(member -> {

            user.openPrivateChannel().queue(
                    privateChannel -> {

                        try {
                            for (int i = 0; i < times; i++) {
                                privateChannel.sendMessage(message).queue(
                                        null, // No success handler needed
                                        dmError -> event.getChannel().sendMessage(
                                                "⚠️ Note: Could not DM " + user.getAsMention() + " (they may have DMs disabled)"
                                        ).queue()
                                );
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    dmError -> event.getChannel().sendMessage(
                            "⚠️ Note: Could not DM " + user.getAsMention() + " (DMs closed to server members)"
                    ).queue()
            );
            System.out.println("command is done :D");
        });
    }

}
