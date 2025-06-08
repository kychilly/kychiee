package com.kychilly.DiscordBot;

import com.kychilly.DiscordBot.commands.CommandManager;
//import com.kychilly.DiscordBot.commands.ReminderCommand;
import com.kychilly.DiscordBot.listeners.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

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
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);


        shardManager = builder.build();

        //register listeners
        shardManager.addEventListener(new PingCommands());
        //shardManager.addEventListener(new MemberJoin());
        shardManager.addEventListener(new SlashCommands());
        shardManager.addEventListener(new CommandManager());
        shardManager.addEventListener(new Typeracer());
        shardManager.addEventListener(new ReminderCommand());
        shardManager.addEventListener(new TimerCommand());
        shardManager.addEventListener(new TyperacerListener());
        shardManager.addEventListener(new TyperacerListener());
        //shardManager.addEventListener(new RuleCommandListener());

        //shardManager.addEventListener(new RussianRouletteListener());
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
        } catch (LoginException e) {
            System.out.println("lol your bot token is wrong");
        } catch (Exception e) {
            System.out.println("your bot has a start up error D:");
            e.printStackTrace();
        }
//# 📜・Server Rules
//
//Welcome to the server! To keep this community safe, respectful, and focused, please follow the rules below:
//
//---
//
//## 1. 🧍‍♂️ Be Respectful
//Treat all members with kindness and professionalism. No hate speech, harassment, racism, sexism, or discrimination of any kind.
//
//## 2. 🛠️ Keep It Professional
//This is a development-focused server. Avoid trolling, drama, or immature behavior.
//
//## 3. 🚫 No Spamming
//Don’t spam messages, pings, emojis, images, or links. No unsolicited advertisements or invites.
//
//## 4. 🧵 Use Channels Correctly
//Post in the correct channels and stay on topic. Check pinned messages or channel descriptions when in doubt.
//
//## 5. 🚷 No NSFW or Inappropriate Content
//Keep all content clean and safe for work — including usernames, profile pictures, and shared media.
//
//## 6. 🔐 Respect Privacy
//Do not share private or sensitive information — yours or anyone else's.
//
//## 7. 📜 Follow Discord's Terms of Service
//Any activity that violates Discord's ToS will result in immediate action.
//
//## 8. 🛡️ Respect Staff Decisions
//Admins and moderators are here to help. Follow their instructions and cooperate if contacted.
//
//---
//
//## ⚠️ Enforcement
//
//- 🟡 Minor offenses may result in warnings.
//- 🔴 Repeated or severe violations can lead to a kick or permanent ban.
//- 🚫 Major offenses (e.g., harassment, doxxing, NSFW posts) = **Instant ban**.
//
//---
//
//✅ By staying in this server, you agree to follow these rules.
//
//Thanks for keeping this community awesome!
    }
}