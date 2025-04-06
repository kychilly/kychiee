package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventListener extends ListenerAdapter {

    private boolean massiveRunning = false;
    private int mainChannelIndex = 2;
    private boolean playingRoulette = false;
    private boolean[] gun = new boolean[6];
    private boolean alive = true;
    private int timesSurvived = 0;
    private boolean ban = false;


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


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw(); // Works here

        System.out.println("I have received a message: " + message);

        if (playingRoulette) {
            if (message.equals("let me see the chamber")) {
                if (hasAdminPerms(event)) {
                    displayGunChamber(event);
                } else {
                    event.getChannel().sendMessage("you have no perms nerd").queue();
                }
            }
            if (message.equals("s")) {
                rouletteSpin(event);
            } else if (message.equals("f")) {
                rouletteFire(event);
            }
            return;
        }

        if (message.charAt(0) == '!') {
            if (message.equals("!ping eliezer")) {
                pingEliezer(event);
            }
        }

        if (message.equals("!russian roulette")) {
            //singleRussianRoulette(event);
            defaultRouletteSettings(event);
        } else if (message.equals("!russian roulette yashi")) {
            rigTheGun5(event);
        } else if (message.equals("!russian roulette crack")) {
            defaultRouletteSettings(event);
            //you get banned if you lose lol
            ban = true;
        }

        if (message.contains("skibidi")) {
            System.out.println("working");
            event.getChannel().sendMessage("dop dop dop yes yes").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
    }

    public void pingEliezer (MessageReceivedEvent event) {
        if (!massiveRunning) {
            massiveRunning = true;
            int delayTimer = 50;
            long eliezer = 976231813037056040L;
            String eliezerID = "<@" + eliezer + ">";
            int totalTextChannels = event.getGuild().getTextChannels().size();

            for (int p = 0; p < 420; p++) {
                for (int i = 3; i < totalTextChannels-1; i++) {
                    try {
                        event.getGuild().getTextChannels().get(i).sendMessage(eliezerID).queue();
                        Thread.sleep(delayTimer);
                    } catch (Exception e) {
                        System.out.println("I AM NOT HAPPY");
                    }
                }
            }
            massiveRunning = false;
            event.getGuild().getTextChannels().get(2).sendMessage("finished!").queue();
        }
    }

//do not use this, it is sussy
    public void singleRussianRoulette(MessageReceivedEvent event) {
        boolean responded = false;
        //int timesSurvived = 0;
        playingRoulette = true;
        gun[(int)(Math.random()*6)] = true;
        while (!alive) {
            responded = false;
            event.getGuild().getTextChannels().get(mainChannelIndex).sendMessage("Would you like to [s]pin or [f]ire?").queue();
            //scanner stuff here, implement
            String choice = "";
            if (choice.equalsIgnoreCase("f")) {
               rouletteFire(event);
            } else if (choice.equalsIgnoreCase("s")) {
                rouletteSpin(event);
            } else {
                System.out.println("im too lazy to code this");
            }
        }
    }

    public void rouletteFire(MessageReceivedEvent event) {
        if (gun[0]) {
            alive = false;
            event.getChannel().sendMessage("Womp womp, you blasted your cabeza out :boom:. You survived for " + timesSurvived + " rounds :XDFUNNYBRO: .").queue();//XDFUNNYBRO is a temp fix
            playingRoulette = false;
            if (ban) {
                banForRoulette(event);
            }
        } else {
            moveBullet(gun);
            timesSurvived++;
            event.getChannel().sendMessage("Woah, you live to see another day :face_exhaling:. You have lived for " + timesSurvived + " times. " + "Would you like to [s]pin or [f]ire?").queue();
        }
    }



    public void rouletteSpin(MessageReceivedEvent event) {
        for (int i = 0; i < 6; i++) {
            int random = (int)(Math.random()*6);
            boolean temp = gun[i];
            gun[i] = gun[random];
            gun[random] = temp;
        }
        event.getChannel().sendMessage("You have spun the chamber. Would you like to [s]pin or [f]ire?").queue();
        //rouletteFire(event);
    }


    public void moveBullet(boolean[] a) {
        boolean temp = a[0];
        for (int i = 1; i < a.length; i++) {
            a[i-1] = a[i];
        }
        a[a.length-1] = temp;
    }

    public void displayGunChamber(MessageReceivedEvent event) {
        String listt = "";
        for (int i = 0; i < 6; i++) {
            listt += gun[i] + " ";
        }
        event.getChannel().sendMessage(listt).queue();
    }

    public void defaultRouletteSettings(MessageReceivedEvent event) {
        timesSurvived = 0;
        playingRoulette = true;
        for (int i = 0; i < 6; i++) {
            gun[i] = false;
        }
        gun[(int)(Math.random()*6)] = true;
        event.getChannel().sendMessage("Would you like to [s]pin or [f]ire?").queue();
    }

    public void rigTheGun5(MessageReceivedEvent event) {
        defaultRouletteSettings(event);
        for (int i = 0; i < 6; i++) {
            gun[i] = true;
        }
        gun[(int)(Math.random()*6)] = false;
    }

    public void banForRoulette(MessageReceivedEvent event) {
        try {
            Member target = event.getMember();
            event.getGuild().ban(target, 0, TimeUnit.DAYS)
                    .reason("Lost a game of Russian Roulette")
                    .queue(
                            (success) -> {
                                event.getChannel().sendMessage("Banned " + target.getUser().getAsTag() +
                                        " for losing a game of Russian roulette").queue();
                            },
                            (error) -> {
                                event.getChannel().sendMessage("Failed to ban: " + error.getMessage()).queue();
                            }
                    );
        } catch (Exception e) {
            event.getChannel().sendMessage("Normally this guy would get banned but hes an admin so he cannot get bozoed out of this server.").queue();
        }
        ban = false;
    }

    public boolean hasAdminPerms(MessageReceivedEvent event) {
        return event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }

}
