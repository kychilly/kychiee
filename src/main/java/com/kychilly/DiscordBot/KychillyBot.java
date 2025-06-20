package com.kychilly.DiscordBot;

import com.kychilly.DiscordBot.classes.ShutdownHandler;
import com.kychilly.DiscordBot.commands.CommandManager;
//import com.kychilly.DiscordBot.commands.ReminderCommand;
import com.kychilly.DiscordBot.listeners.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class KychillyBot {

    private final ShardManager shardManager;
    private final Dotenv config;

    private static final String CHANNEL_ID = "1186115783013711894";


    public KychillyBot() throws LoginException {
        config = Dotenv.configure().ignoreIfMissing().load();
        String token = config.get("TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("the villagers"));
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
        shardManager.addEventListener(new TimerCommand());
        shardManager.addEventListener(new TyperacerListener());
        shardManager.addEventListener(new ShutdownListener());
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


    }
}