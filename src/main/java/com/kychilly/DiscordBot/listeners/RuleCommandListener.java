package com.kychilly.DiscordBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;


public class RuleCommandListener extends ListenerAdapter {

    private static final long RULES_CHANNEL_ID = 1378587556072919161L;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (message.getAuthor().isBot()) return;

        if (content.equalsIgnoreCase("!rules")) {
            JDA jda = event.getJDA();
            GuildMessageChannel rulesChannel = jda.getChannelById(GuildMessageChannel.class, RULES_CHANNEL_ID);

            if (rulesChannel == null) {
                event.getChannel().sendMessage("⚠️ Could not find the rules channel.").queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("📜 Server Rules");
            embed.setDescription("Follow these rules to keep the server professional and safe.");
            embed.setColor(Color.BLUE);

            embed.addField("1. 🧍‍♂️ Be Respectful", "No hate speech, harassment, or toxic behavior.", false);
            embed.addField("2. 🛠️ Keep It Professional", "Avoid drama, trolling, or immature behavior.", false);
            embed.addField("3. 🚫 No Spamming", "No message floods, pings, or unsolicited ads.", false);
            embed.addField("4. 🧵 Use Channels Correctly", "Post in the appropriate channels only.", false);
            embed.addField("5. 🚷 No NSFW or Inappropriate Content", "Keep everything SFW, including names and media.", false);
            embed.addField("6. 🔐 Respect Privacy", "Don’t share anyone’s private info.", false);
            embed.addField("7. 📜 Follow Discord's Terms of Service", "No ToS violations allowed.", false);
            embed.addField("8. 🛡️ Respect Staff Decisions", "Staff decisions are final. Cooperate when asked.", false);

            embed.setFooter("\uD83E\uDD88 For development-related questions, please ping a developer.");

            rulesChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
