package com.kychilly.DiscordBot;

import com.kychilly.DiscordBot.listeners.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;

public class KychillyBot {

    private final ShardManager shardManager;
    private final Dotenv config;

    public KychillyBot() throws LoginException {
        config = Dotenv.configure().ignoreIfMissing().load();
        String token = config.get("TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("the sunflowers"));
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        shardManager = builder.build();

        //register listener
        shardManager.addEventListener(new EventListener());
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public Dotenv getConfig() {
        return config;
    }

    public static void main(String[] args) {
        try {
            KychillyBot bot = new KychillyBot();
        } catch (Exception e) {
            System.out.println("Provided bot token is incorrect");
        }
    }
}