package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactWithReaction extends ListenerAdapter {

    @Override
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
        User user = event.getUser();
        if (user == null) {
            return;
        }

        Guild guild = event.getGuild();


        String emoji = event.getReaction().getEmoji().getAsReactionCode();
        String channelMention = event.getChannel().getAsMention();
        String jumpLink = event.getJumpUrl();
        String message = user.getAsMention() + " reacted to message with " + emoji + " in channel " + channelMention + "!";
        DefaultGuildChannelUnion defaultchan = event.getGuild().getDefaultChannel();
        defaultchan.asTextChannel().sendMessage(message).queue();
    }

}
