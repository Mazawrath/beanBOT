package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.SentryLog;
import com.mazawrath.beanbot.utilities.Twitch;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

public class AdminTwitch implements CommandExecutor {
    private Twitch twitch;

    public AdminTwitch(Twitch twitch) {
        this.twitch = twitch;
    }

    @Command(
            aliases = {"AdminTwitch"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        SentryLog.addContext(args, author, server);

        if (!author.isBotOwner() && !server.isOwner(author)) {
//            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            int subscriptionStatus = twitch.addServer(args[1], server.getIdAsString(), serverTextChannel.getIdAsString());
            if (subscriptionStatus == 1)
                serverTextChannel.sendMessage("Subscribed to live notifications for " + args[1] + ".");
             else if (subscriptionStatus == 0)
                serverTextChannel.sendMessage("Could not subscribe to " + args[1] + ".");
             else
                 serverTextChannel.sendMessage("Could not subscribe to " + args[1] + ". You are already subscribed to livestream " +
                         "notifications for another channel. Use `.admintwitchsettings remove` to unsubscribe from those notifications.");
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (twitch.removeServer(server.getIdAsString()))
                serverTextChannel.sendMessage("Unsubscribed from live notifications.");
            else
                serverTextChannel.sendMessage("Could not unsubscribe from live notifications. Most likely you are not subscribed to " +
                        "live notifications for a channel. Use `.admintwitchsettings add [channel name]` to subscribe for livestream notifications for that channel.");
        } else if (args[0].equals("set")) {
            String serverTextChannelId = args[1].substring(2,args[1].length() -1);
            if (api.getServerTextChannelById(serverTextChannelId).isPresent()) {
                if (twitch.setChannel(server.getIdAsString(), serverTextChannelId))
                serverTextChannel.sendMessage("Notification channel set to " + api.getServerTextChannelById(serverTextChannelId).get().getName() + ".");
                else
                    serverTextChannel.sendMessage("Could not set a text channel for live notifications. Most likely you are not subscribed to " +
                            "live notifications for a twitch channel. Use `.admintwitchsettings add [channel name]` to subscribe for livestream notifications for that channel.");
            } else
                serverTextChannel.sendMessage("Invalid channel.");
        }

        Sentry.clearContext();
    }
}
