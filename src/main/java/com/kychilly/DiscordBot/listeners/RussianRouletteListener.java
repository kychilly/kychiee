package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class RussianRouletteListener extends ListenerAdapter {

    private boolean playingRoulette = false;
    private boolean[] gun = new boolean[6];
    private boolean alive = true;
    private int timesSurvived = 0;
    private boolean ban = false;
    private Member memberPlayingRoulette;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();

        if (playingRoulette && memberPlayingRoulette.equals(event.getMember())) {
            handleRouletteInput(event, message);
        } else {
            checkForRouletteStart(event, message);
        }
    }

    private void checkForRouletteStart(MessageReceivedEvent event, String message) {
        if (message.equals("!russian roulette")) {
            defaultRouletteSettings(event);
        } else if (message.equals("!russian roulette yashi")) {
            rigTheGun5(event);
        } else if (message.equals("!russian roulette crack")) {
            defaultRouletteSettings(event);
            ban = true;
        } else if ((message.equals("s") || message.equals("f")) && playingRoulette) {
            event.getChannel().sendMessage("Sorry, " + memberPlayingRoulette.getNickname() +
                    " is currently playing russian roulette. Please wait your turn.").queue();
        }
    }

    private void handleRouletteInput(MessageReceivedEvent event, String message) {
        if (message.equals("let me see the chamber")) {
            if (hasAdminPerms(event)) {
                displayGunChamber(event);
            } else {
                event.getChannel().sendMessage("You have no permissions to view the chamber.").queue();
            }
            return;
        }

        if (message.equals("s")) {
            rouletteSpin(event);
        } else if (message.equals("f")) {
            rouletteFire(event);
        }
    }

    private void rouletteFire(MessageReceivedEvent event) {
        if (gun[0]) {
            alive = false;
            event.getChannel().sendMessage(memberPlayingRoulette.getAsMention() +
                    " BOOM HEADSHOT :boom:. You survived for " + timesSurvived +
                    " rounds <:XDFUNNYBRO:1322029721210589235>").queue();
            playingRoulette = false;
            memberPlayingRoulette = null;
            if (ban) {
                banForRoulette(event);
            }
        } else {
            moveBullet(gun);
            timesSurvived++;
            event.getChannel().sendMessage(memberPlayingRoulette.getAsMention() +
                    " Woah, you live to see another day :face_exhaling:. You have lived for " +
                    timesSurvived + " times. Would you like to [s]pin or [f]ire?").queue();
        }
    }

    private void rouletteSpin(MessageReceivedEvent event) {
        for (int i = 0; i < 6; i++) {
            int random = (int)(Math.random()*6);
            boolean temp = gun[i];
            gun[i] = gun[random];
            gun[random] = temp;
        }
        event.getChannel().sendMessage(memberPlayingRoulette.getAsMention() +
                " You have spun the chamber. Would you like to [s]pin or [f]ire?").queue();
    }

    private void moveBullet(boolean[] chamber) {
        boolean temp = chamber[0];
        System.arraycopy(chamber, 1, chamber, 0, chamber.length - 1);
        chamber[chamber.length-1] = temp;
    }

    private void displayGunChamber(MessageReceivedEvent event) {
        StringBuilder chamberState = new StringBuilder();
        for (boolean b : gun) {
            chamberState.append(b).append(" ");
        }
        event.getChannel().sendMessage(chamberState.toString()).queue();
    }

    private void defaultRouletteSettings(MessageReceivedEvent event) {
        memberPlayingRoulette = event.getMember();
        timesSurvived = 0;
        playingRoulette = true;
        for (int i = 0; i < 6; i++) {
            gun[i] = false;
        }
        gun[(int)(Math.random()*6)] = true;
        event.getChannel().sendMessage(memberPlayingRoulette.getAsMention() +
                " Would you like to [s]pin or [f]ire?").queue();
    }

    private void rigTheGun5(MessageReceivedEvent event) {
        defaultRouletteSettings(event);
        for (int i = 0; i < 6; i++) {
            gun[i] = true;
        }
        gun[(int)(Math.random()*6)] = false;
    }

    private void banForRoulette(MessageReceivedEvent event) {
        try {
            Member target = event.getMember();
            event.getGuild().ban(target, 0, TimeUnit.DAYS)
                    .reason("Lost a game of Russian Roulette")
                    .queue(
                            (success) -> event.getChannel().sendMessage("Banned " +
                                    target.getUser().getAsTag() +
                                    " for losing a game of Russian roulette").queue(),
                            (error) -> event.getChannel().sendMessage(
                                    "Failed to ban: " + error.getMessage()).queue()
                    );
        } catch (Exception e) {
            event.getChannel().sendMessage(
                    "Normally this user would get banned but they're an admin so they cannot be banned.").queue();
        }
        ban = false;
    }

    private boolean hasAdminPerms(MessageReceivedEvent event) {
        return event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }
}