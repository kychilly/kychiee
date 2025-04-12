package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MessageSent extends ListenerAdapter {

    private boolean massiveRunning = false;
    private int mainChannelIndex = 2;
    private boolean playingRoulette = false;
    private boolean[] gun = new boolean[6];
    private boolean alive = true;
    private int timesSurvived = 0;
    private boolean ban = false;
    private boolean PLEASESTOP = false;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;  // Ignore the message if it's from a bot
        }

        String message = event.getMessage().getContentRaw(); // Works here
        System.out.println("I have received a message: " + message);
        if (playingRoulette) {
            playingRussianRoulette(event, message);
        }
        //if a game is already playing, do nothing
        tryAllRussianRoulettes(event, message);


        //the mass pinging
        if (message.charAt(0) == '!') {
            if (message.equalsIgnoreCase("!PLEASE STOP") && hasAdminPerms(event)) {
                PLEASESTOP = true;
            } else if (message.equalsIgnoreCase("!PLEASE GO") && hasAdminPerms(event)) {
                PLEASESTOP = false;
                event.getChannel().sendMessage("You may now use !pinguser.").queue();
            }

            if (message.startsWith("!pinguser")) {
                if (!massiveRunning) {
                    String ping = findGuyToPing(event, message);
                    new Thread(() -> {
                        massPingUser(event, ping);
                    }).start();
                } else {
                    event.getChannel().sendMessage("Sorry! This command is already running. Please wait until it finishes to use it again.").queue();
                }
            } else if (message.startsWith("!pfp")) {
                getUserPfp(event, message.substring(4));
            } else if (message.startsWith("!ban")) {
                //you know what comes here

            }
        }

        if (message.contains("skibidi")) {
            System.out.println("working");
            event.getChannel().sendMessage("dop dop dop yes yes").queue();
        }
    }






    public void massPingUser (MessageReceivedEvent event, String pinged) {
        if (pinged.equals("n/a")) {
            event.getChannel().sendMessage("Sorry, this user doesn't exist in this server!").queue();
            return;
        }
            massiveRunning = true;
            int delayTimer = 50;
            int totalTextChannels = event.getGuild().getTextChannels().size();
            for (int p = 0; p < 420; p++) {
                for (int i = 3; i < totalTextChannels-1; i++) {
                    try {
                        if (!PLEASESTOP) {
                            event.getGuild().getTextChannels().get(i).sendMessage(pinged).queue();
                            Thread.sleep(delayTimer);
                        } else {
                            event.getChannel().sendMessage("Stopping !pinguser command").queue();
                            massiveRunning = false;
                            return;
                        }
                    } catch (Exception e) {
                        System.out.println("I AM NOT HAPPY");
                        event.getChannel().sendMessage("Oops! Something happened. Stopping command, please fix kyche.").queue();
                        break;
                    }
                }
            }
            massiveRunning = false;
            event.getGuild().getTextChannels().get(mainChannelIndex).sendMessage("finished!").queue();
    }

    public void getUserPfp(MessageReceivedEvent event, String message) {
        if (message.isEmpty()) {
            String avatar = event.getAuthor().getAvatarUrl();
            event.getChannel().sendMessage(avatar).queue();
        } else {
            findUserByUsername(event, message.substring(1), user -> {
                String avatar = user.getEffectiveAvatarUrl();
                event.getChannel().sendMessage(avatar).queue();
            }, () -> {
                event.getChannel().sendMessage("User `" + message + "` not found in this server.").queue();
            });
        }
    }

    //PLEASE REMEMBER TO CODE THIS
    public void banUser(MessageReceivedEvent event, String guy) {
        if (hasAdminPerms(event)) {

        }
    }

    public void rouletteFire(MessageReceivedEvent event) {
        if (gun[0]) {
            alive = false;
            event.getChannel().sendMessage("Womp womp, you blasted your cabeza out :boom:. You survived for " + timesSurvived + " rounds <:XDFUNNYBRO:1322029721210589235> .").queue();//XDFUNNYBRO is a temp fix
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

    public void tryAllRussianRoulettes(MessageReceivedEvent event, String message) {
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
    }

    public void playingRussianRoulette(MessageReceivedEvent event, String message) {
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

    public String findGuyToPing(MessageReceivedEvent event, String message) {
        // Extract the username to search for
        String searchUsername = message.substring("!pinguser".length()).trim();

        if (searchUsername.isEmpty()) {
            event.getChannel().sendMessage("Please provide a username to search for.").queue();
            return "n/a";
        }

        // Get all members in the server
        List<Member> members = event.getGuild().getMembers();
        // Search for the member
        Member foundMember = null;
        for (Member member : members) {
            String username = member.getUser().getName();
            String discriminator = member.getUser().getDiscriminator();
            String fullUsername = username + "#" + discriminator;

            // Check if username matches (case insensitive)
            if (fullUsername.equalsIgnoreCase(searchUsername) ||
                    username.equalsIgnoreCase(searchUsername)) {
                foundMember = member;
                break;
            }
        }

        // Respond based on whether user was found
        if (foundMember != null) {
            return foundMember.getAsMention();
        } else {
            return "n/a";
        }
    }

    //sussy chatgpt User return method
    public void findUserByUsername(MessageReceivedEvent event, String inputUsername, Consumer<User> onFound, Runnable onNotFound) {
        Guild guild = event.getGuild();

        // Fetch members matching the username or tag
        guild.findMembers(member -> {
            User user = member.getUser();
            String username = user.getName(); // New-style username (no #1234)
            String fullTag = user.getAsTag(); // Old-style tag (username#1234)

            return username.equalsIgnoreCase(inputUsername) || fullTag.equalsIgnoreCase(inputUsername);
        }).onSuccess(members -> {
            if (!members.isEmpty()) {
                User matchedUser = members.get(0).getUser();
                onFound.accept(matchedUser); // Pass the found user to the callback
            } else {
                onNotFound.run(); // Trigger the not-found callback
            }
        }).onError(error -> {
            onNotFound.run();
        });
    }

    public boolean hasAdminPerms(MessageReceivedEvent event) {
        return event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }

}
