//genuinely no idea why this doesnt not at all D:

package com.kychilly.DiscordBot.classes;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import okhttp3.RequestBody;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.requests.Route;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import org.json.JSONObject;

public class ShutdownHandler {
    private final ShardManager shardManager;
    private final String channelId;
    private final OkHttpClient httpClient;

    public ShutdownHandler(ShardManager shardManager, String channelId) {
        this.shardManager = shardManager;
        this.channelId = channelId;
        this.httpClient = new OkHttpClient();
        registerShutdownHook();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Try normal JDA method first (works in some cases)
            tryNormalShutdownNotification();

            // If that fails, use raw HTTP
            tryRawHttpNotification();
        }));
    }

    private void tryNormalShutdownNotification() {
        try {
            for (JDA shard : shardManager.getShards()) {
                if (shard.getStatus() == JDA.Status.CONNECTED) {
                    TextChannel channel = shard.getTextChannelById(channelId);
                    if (channel != null) {
                        // Use submit() with timeout instead of complete()
                        channel.sendMessage(":warning: Bot is shutting down!")
                                .submit()
                                .get(2, TimeUnit.SECONDS);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Normal shutdown notification failed: " + e.getMessage());
        }
    }

    private void tryRawHttpNotification() {
        try {
            JSONObject json = new JSONObject()
                    .put("content", "⚠️ Emergency bot shutdown detected!");

            Request request = new Request.Builder()
                    .url(Route.Messages.SEND_MESSAGE.compile(channelId).getCompiledRoute())
                    .post(RequestBody.create(
                            json.toString(),
                            MediaType.get("application/json")
                    ))
                    .header("Authorization", "Bot ")
                    .header("User-Agent", "DiscordBot (https://github.com/discord-jda/JDA)")
                    .build();

            // Synchronous execute - we're in shutdown, no async needed
            httpClient.newCall(request).execute().close();
        } catch (Exception e) {
            System.err.println("Raw HTTP shutdown notification failed: " + e.getMessage());
        } finally {
            httpClient.dispatcher().executorService().shutdown();
        }
    }
}