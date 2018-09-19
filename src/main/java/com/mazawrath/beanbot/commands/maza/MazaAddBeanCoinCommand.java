package com.mazawrath.beanbot.commands.maza;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class MazaAddBeanCoinCommand implements CommandExecutor {
    private Points points;

    public MazaAddBeanCoinCommand(Points pointsPassed) {
        points = pointsPassed;
    }

    @Command(
            aliases = {"mazaaddbeancoin"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String command, String user, String pointValue, ServerTextChannel serverTextChannel, User author, Server server) {
        if (author.isBotOwner()) {
            serverTextChannel.sendMessage("Added " + pointValue + " beanCoin to " + user + ".");

            points.addPoints(user, server.getIdAsString(), Integer.parseInt(pointValue));
        }
    }
}
