package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class Z_RepuestaChecker extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getGuild().getIdLong() == 1186115782313267321L && event.getMessage().getContentRaw().contains("repuesta")) {
            Member memberToMute = event.getGuild().getMemberById("1295872060341616640");
            Duration timeoutDuration = Duration.ofSeconds(3);
            assert memberToMute != null;
            event.getGuild()
                    .timeoutFor(memberToMute, timeoutDuration)
                    .queue(
                            success -> event.getChannel().sendMessage(event.getGuild().getMemberById("739978476651544607").getAsMention() + " dame repuestas").queue(),
                            error -> event.getChannel().sendMessage("Failed to mute: " + error.getMessage()).queue()
                    );
        }
    }
}
