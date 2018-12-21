package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.Lottery;
import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.Twitch;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

public class AdminTwitchSettings implements CommandExecutor {
    private Twitch twitch;

    public AdminTwitchSettings(Twitch twitch) {
        this.twitch = twitch;
    }

    @Command(
            aliases = {"AdminTwitchSettings"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        if (!author.isBotOwner() && !server.isOwner(author)) {
            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
            return;
        }

        if (args[0].equals("subscribe")) {
            if (twitch.subscribeToLiveNotfications(args[1], server.getIdAsString()))
                serverTextChannel.sendMessage("Subscribe to live notifications for" + args[1] + ".");
            else
                serverTextChannel.sendMessage("Could not subscribe to " + args[1] + ".");
        } else if (args[0].equals("unsubscribe")) {
            if (twitch.unsubscribeFromLiveNotfications(args[1], server.getIdAsString()))
                serverTextChannel.sendMessage("Subscribe to live notifications for" + args[1] + ".");
            else
                serverTextChannel.sendMessage("Could not subscribe to " + args[1] + ".");
        } else if (args[0].equals("notificationChannel")) {
            // TODO Finish this thing
        }
    }
}
