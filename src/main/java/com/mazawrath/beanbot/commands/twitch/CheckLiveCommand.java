package com.mazawrath.beanbot.commands.twitch;

import com.mazawrath.beanbot.utilities.Twitch;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class CheckLiveCommand implements CommandExecutor {
    private Twitch twitch;

    public CheckLiveCommand(Twitch twitch) {
        this.twitch = twitch;
    }

    @Command(
            aliases = {"checklive"},
            description = "Checks if a twitch channel is live.",
            usage = "checklive [channel]",
            privateMessages = false
    )

    public void onCommand(String[] args, ServerTextChannel serverTextChannel, User author, Server server) {
        if (twitch.checkIfLive(args[0])) {
            serverTextChannel.sendMessage(args[0] + " is live");
        } else
            serverTextChannel.sendMessage(args[0] + " is not live");
    }
}
