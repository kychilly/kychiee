package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PingCommands extends ListenerAdapter {

    private boolean massiveRunning = false;
    private int mainChannelIndex = 2;
    private boolean PLEASESTOP = false;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }


        String message = event.getMessage().getContentRaw();

        //the mass pinging
        if (message.charAt(0) == '!') {
            if (message.equalsIgnoreCase("!PLEASE STOP") && hasAdminPerms(event)) {
                PLEASESTOP = true;
            } else if (message.equalsIgnoreCase("!PLEASE GO") && hasAdminPerms(event)) {
                PLEASESTOP = false;
                event.getChannel().sendMessage("You may now use !pinguser.").queue();
            }

            if (message.startsWith("!ghostping")) {
                String ping = findGuyToPing(event, message);
                ghostPingUser(event, ping);
            }

            if (message.startsWith("!pinguser")) {//do not use this command for legal purposes
//                if (event.getAuthor().getIdLong() == 840216337119969301L) {
//                if (!massiveRunning) {
//                    String ping = findGuyToPing(event, message);
//                    System.out.println(ping);
//                    new Thread(() -> {
//                        massPingUser(event, ping);
//                    }).start();
//                } else {
//                    event.getChannel().sendMessage("Sorry! This command is already running. Please wait until it finishes to use it again.").queue();
//                }
//                }
            } else if (message.startsWith("!pfp")) {
                getUserPfp(event, message.substring(4));
            }
        }

        if (message.contains("skibidi")) {
            event.getChannel().sendMessage("dop dop dop yes yes").queue();
        }
        if (message.contains("cat") && event.getGuild().getId().equals("722009112921243678")) {
            meowCommand(event);
        }

    }

    public void massPingUser(MessageReceivedEvent event, String pinged) {
        if (pinged.equals("n/a")) {
            event.getChannel().sendMessage("Sorry, this user doesn't exist in this server!").queue();
            return;
        }
        massiveRunning = true;
        int delayTimer = 25;
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

    public void ghostPingUser(MessageReceivedEvent event, String theUserMention) {
        if (theUserMention.equals("n/a")) {
            event.getChannel().sendMessage("Sorry, this user doesn't exist in this server!").queue();
            return;
        }
        try {
            event.getMessage().delete().queue();
            for (int i = 0; i < 5; i++) {
                event.getChannel().sendMessage(theUserMention).queue(message -> {
                    // Schedule deletion after 2 seconds
                    message.delete().queueAfter(2, TimeUnit.SECONDS,
                            success -> {
                            },
                            error -> System.out.println("Couldn't delete message: " + error.getMessage())
                    );
                });
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
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

    public void banUser(MessageReceivedEvent event, String guy) {
        if (hasAdminPerms(event)) {
            // Ban implementation
        }
    }

    public void meowCommand(MessageReceivedEvent event) {
        for (int i = 0; i < Math.random()*7 + 3; i++) {
            String meow = "";
            for (int p = 0; p < Math.random()*15 + 1; p++) {
                meow += "meow ";
            }
            try {
                event.getChannel().sendMessage(meow).queue();
                Thread.sleep(400);
            } catch (Exception e) {
                System.out.println("HELP ME");
            }

        }
    }

    //RETURNS THE MENTION
    public String findGuyToPing(MessageReceivedEvent event, String message) {
        String searchUsername = message.split("\\s+")[1];

        if (searchUsername.isEmpty()) {
            event.getChannel().sendMessage("Please provide a username to search for.").queue();
            return "n/a";
        }

        // Clean input
        searchUsername = searchUsername.replace("@", "").trim().toLowerCase();

        for (Member member : event.getGuild().getMembers()) {
            String username = member.getUser().getName().toLowerCase();
            String nickname = member.getNickname();

            if (username.equals(searchUsername) ||
                    (nickname != null && nickname.toLowerCase().equals(searchUsername))) {
                return member.getAsMention();
            }
        }

        return "n/a";
    }

    public void findUserByUsername(MessageReceivedEvent event, String inputUsername, Consumer<User> onFound, Runnable onNotFound) {
        Guild guild = event.getGuild();

        // Fetch members matching the username or tag
        guild.findMembers(member -> {
            User user = member.getUser();
            String username = user.getName();
            String fullTag = user.getAsTag();

            return username.equalsIgnoreCase(inputUsername) || fullTag.equalsIgnoreCase(inputUsername);
        }).onSuccess(members -> {
            if (!members.isEmpty()) {
                User matchedUser = members.get(0).getUser();
                onFound.accept(matchedUser);
            } else {
                onNotFound.run();
            }
        }).onError(error -> {
            onNotFound.run();
        });
    }

    public boolean hasAdminPerms(MessageReceivedEvent event) {
        return event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }
}