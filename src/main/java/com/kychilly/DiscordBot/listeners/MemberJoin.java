package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberJoin extends ListenerAdapter {
    private int mainChannelIndex = 2;



    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        String avatar = event.getUser().getEffectiveAvatarUrl();
        event.getGuild().getTextChannels().get(mainChannelIndex).sendMessage("Welcome " + event.getMember().getAsMention() + ", we hope you enjoy your stay! " + avatar).queue();
    }


}
