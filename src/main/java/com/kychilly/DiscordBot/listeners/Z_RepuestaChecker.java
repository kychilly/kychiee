package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

public class Z_RepuestaChecker extends ListenerAdapter {


    public static void RepuestaCommand(MessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() == 1321967650557005888L) return; // Return so my bot doesnt keep saying it

        // If it is in image bot and the message says repuesta, automatically time out ramblebot
        if (event.getGuild().getIdLong() == 1186115782313267321L && event.getMessage().getContentRaw().contains("repuesta")) {
            Member memberToMute = event.getGuild().getMemberById("1295872060341616640"); // This is ramblebot
            String noob = Objects.requireNonNull(event.getGuild().getMemberById("739978476651544607")).getAsMention();
            Duration timeoutDuration = Duration.ofSeconds(3);
            assert memberToMute != null;
            event.getGuild()
                    .timeoutFor(memberToMute, timeoutDuration)
                    .queue(
                            success -> event.getChannel().sendMessage("Timed out this noob ahh ramblebot\n" + noob + " no repuestas = no es simpatico").queue(),
                            error -> event.getChannel().sendMessage("Failed to mute: " + error.getMessage()).queue()
                    );
        }
    }
}
