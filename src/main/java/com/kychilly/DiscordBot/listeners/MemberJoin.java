package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MemberJoin extends ListenerAdapter {
    private int mainChannelIndex = 2;



    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(event.getUser().getName());
            eb.setImage(event.getUser().getEffectiveAvatarUrl() + "?size=4096"); // Max resolution
            eb.setColor(Color.CYAN);
        event.getGuild().getSystemChannel().sendMessageEmbeds(eb.build()).queue();
    }


}
