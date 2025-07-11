package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotReadyListener extends ListenerAdapter {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();

        Runnable updateStatus = () -> {
            int totalGuilds = jda.getGuilds().size();
            long totalMembers = jda.getGuilds().stream()
                    .mapToLong(Guild::getMemberCount)
                    .sum();

            String activityText = String.format("%d users in %d servers", totalMembers, totalGuilds);

            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.watching(activityText));

            System.out.println("Updated activity to: " + activityText);
        };

        // Run immediately once onReady
        updateStatus.run();

        // Schedule to run every 1 hour (3600 seconds)
        scheduler.scheduleAtFixedRate(updateStatus, 1, 1, TimeUnit.HOURS);
    }
}