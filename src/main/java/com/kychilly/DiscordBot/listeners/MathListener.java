package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MathListener extends ListenerAdapter {

    private static HashMap<Long, Boolean> map = new HashMap<>();
    public int[] answer = new int[1];

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        long channelId = event.getGuildChannel().getIdLong();


        if (message.equals("!math")) {
            if (map.get(channelId) != null) {
                event.getChannel().sendMessage(event.getMember().getAsMention() + " , there is already a math game in progress!").queue();
                return;
            }
            map.put(channelId, true);
            int random = (int)(Math.random()*4);
            switch (random) {
                case 0:
                    addition(event);
                    break;
                case 1:
                    subtraction(event);
                    break;
                case 2:
                    multiplication(event);
                    break;
                default:
                    division(event);
            }
        } else if (map.get(channelId) != null) {
            try {
                int guess = Integer.parseInt(message);
                if (guess == answer[0]) {
                    returnWinMessage(event);
                }
            } catch (Exception e) {
                return; // means it wasnt a number
            }
        }
    }

    // addition, subtraction. multiplication, division
    public void addition(MessageReceivedEvent event) {
        int ran1 = (int)(Math.random()*10);
        int ran2 = (int)(Math.random()*10);
        event.getChannel().sendMessage(String.format("What is %d + %d?", ran1, ran2)).queue();
        answer[0] = ran1+ran2;
    }

    public void subtraction(MessageReceivedEvent event) {
        int ran1 = (int)(Math.random()*10 + 10);
        int ran2 = (int)(Math.random()*10);
        event.getChannel().sendMessage(String.format("What is %d - %d?", ran1, ran2)).queue();
        answer[0] = ran1-ran2;
    }

    public void multiplication(MessageReceivedEvent event) {
        int ran1 = (int)(Math.random()*10);
        int ran2 = (int)(Math.random()*10);
        event.getChannel().sendMessage(String.format("What is %d * %d?", ran1, ran2)).queue();
        answer[0] = ran1*ran2;
    }

    public void division(MessageReceivedEvent event) {
        int ran2 = (int)(Math.random()*10 + 2);
        int ran1 = ran2 * (int)(Math.random()*10 + 1);
        event.getChannel().sendMessage(String.format("What is %d / %d?", ran1, ran2)).queue();
        answer[0] = ran1/ran2;
    }

    public void returnWinMessage(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(event.getMember().getAsMention() + ", CONGRATS, YOU GOT IT CORRECT!!!!\nTHE ANSWER WAS: " + answer[0]);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
        map.remove(event.getGuildChannel().getIdLong());
        return;
    }

}
