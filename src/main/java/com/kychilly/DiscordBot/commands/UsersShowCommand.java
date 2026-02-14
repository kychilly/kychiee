package com.kychilly.DiscordBot.commands;

import com.kychilly.DiscordBot.utils.UsersManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UsersShowCommand {

    private static final int USERS_PER_PAGE = 10;

    public static CommandData getCommandData() {
        return Commands.slash("showusers", "shows registered users");
    }

    public static void showUsers(SlashCommandInteractionEvent event) {

        Guild guild = event.getGuild();
        Set<Long> usersSet = UsersManager.getUserList(guild);

        if (usersSet.isEmpty()) {
            event.reply("No users saved.").setEphemeral(true).queue();
            return;
        }

        sendPage(event, 0, usersSet);
    }

    private static void sendPage(SlashCommandInteractionEvent event, int page, Set<Long> usersSet) {

        List<Long> users = new ArrayList<>(usersSet);

        int maxPage = (int) Math.ceil(users.size() / (double) USERS_PER_PAGE);
        if (maxPage == 0) maxPage = 1;

        page = Math.max(0, Math.min(page, maxPage - 1));

        int start = page * USERS_PER_PAGE;
        int end = Math.min(start + USERS_PER_PAGE, users.size());

        StringBuilder builder = new StringBuilder();
        builder.append("**Total Users:** ").append(users.size()).append("\n\n");

        for (int i = start; i < end; i++) {
            builder.append("<@").append(users.get(i)).append(">\n");
        }

        builder.append("\nPage ").append(page + 1).append(" / ").append(maxPage);

        Button prev = Button.primary("users_prev_" + page, "◀")
                .withDisabled(page == 0);

        Button next = Button.primary("users_next_" + page, "▶")
                .withDisabled(page >= maxPage - 1);

        event.reply(builder.toString())
                .addActionRow(prev, next)
                .queue();
    }

    public static void handleButton(ButtonInteractionEvent event) {

        if (!event.getComponentId().startsWith("users_"))
            return;

        Guild guild = event.getGuild();
        Set<Long> usersSet = UsersManager.getUserList(guild);
        List<Long> users = new ArrayList<>(usersSet);

        int currentPage = Integer.parseInt(event.getComponentId().split("_")[2]);
        int newPage = currentPage;

        if (event.getComponentId().startsWith("users_next"))
            newPage++;

        if (event.getComponentId().startsWith("users_prev"))
            newPage--;

        int maxPage = (int) Math.ceil(users.size() / (double) USERS_PER_PAGE);
        if (maxPage == 0) maxPage = 1;

        newPage = Math.max(0, Math.min(newPage, maxPage - 1));

        int start = newPage * USERS_PER_PAGE;
        int end = Math.min(start + USERS_PER_PAGE, users.size());

        StringBuilder builder = new StringBuilder();
        builder.append("**Total Users:** ").append(users.size()).append("\n\n");

        for (int i = start; i < end; i++) {
            builder.append("<@").append(users.get(i)).append(">\n");
        }

        builder.append("\nPage ").append(newPage + 1).append(" / ").append(maxPage);

        Button prev = Button.primary("users_prev_" + newPage, "◀")
                .withDisabled(newPage == 0);

        Button next = Button.primary("users_next_" + newPage, "▶")
                .withDisabled(newPage >= maxPage - 1);

        event.editMessage(builder.toString())
                .setActionRow(prev, next)
                .queue();
    }
}
