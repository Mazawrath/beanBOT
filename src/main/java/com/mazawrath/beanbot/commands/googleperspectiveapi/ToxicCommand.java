package com.mazawrath.beanbot.commands.googleperspectiveapi;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.PointsUser;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.mazawrath.beanbot.utilities.pesrspectiveapi_requests.MessageRequest;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class ToxicCommand implements CommandExecutor {
    private Points points;

    public ToxicCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"toxic"},
            usage = "toxic [user]/[message]",
            description = "Detects how toxic a message is",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, Message discordMessage, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        PointsUser user = new PointsUser(author, server);
        String userName = "";
        StringBuilder message = new StringBuilder();

        if (!points.canMakePurchase(user, Points.COMMAND_COST)) {
            serverTextChannel.sendMessage("You do not have enough beanCoin to use this command.");
            return;
        }

        if (args.length != 0) {
            // Get users most recent message
            if (args[0].contains("@")) {
                userName = discordMessage.getMentionedUsers().get(0).getDisplayName(server);

                MessageSet previousMessages = discordMessage.getMessagesBefore(20).join();
                for (Message previousMessage : previousMessages.descendingSet()) {
                    if (previousMessage.getUserAuthor().get().getIdAsString().equals(discordMessage.getMentionedUsers().get(0).getIdAsString()) && !previousMessage.getContent().isEmpty()) {
                        message.append(previousMessage.getContent());
                        break;
                    }
                }
                if (message.toString().isEmpty()) {
                    serverTextChannel.sendMessage("Could not find any recent messages from this person.");
                    return;
                }
            } else {
                userName = author.getDisplayName(server);
                for (int i = 0; i < args.length; i++) {
                    message.append(args[i]);
                    if (i + 1 != args.length)
                        message.append(" ");
                }
            }
        }  else {
            MessageSet previousMessages = discordMessage.getMessagesBefore(20).join();
            for (Message previousMessage : previousMessages.descendingSet()) {
                if (!previousMessage.getContent().isEmpty()) {
                    userName = previousMessage.getUserAuthor().get().getDisplayName(server);
                    message.append(previousMessage.getContent());
                    break;
                }
                if (message.toString().isEmpty()) {
                    serverTextChannel.sendMessage("Could not find any recent messages.");
                    return;
                }
            }
        }

        MessageRequest messageAnalysis = new MessageRequest(message.toString());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Message Analysis")
                .addField("Message", message.toString())
                .addInlineField("Message From", userName)
                .addInlineField("Toxic Probability", String.format("%.0f%%", messageAnalysis.getToxicityProb() * 100))
                .addInlineField("Identity Attack Probability", String.format("%.0f%%", messageAnalysis.getIdentityAttackProb() * 100))
                .addInlineField("Insult Probability", String.format("%.0f%%", messageAnalysis.getInsultProb() * 100))
                .addInlineField("Incoherent Probability", String.format("%.0f%%", messageAnalysis.getIncoherentProb() * 100))
                .addInlineField("Sexually Explicit Probability", String.format("%.0f%%", messageAnalysis.getSexuallyExplicitProb() * 100));
        serverTextChannel.sendMessage(embed);

        points.makePurchase(user, Points.COMMAND_COST);
        Sentry.clearContext();
    }
}
