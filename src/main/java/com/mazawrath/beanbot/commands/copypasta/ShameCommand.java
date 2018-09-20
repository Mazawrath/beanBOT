package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class ShameCommand implements CommandExecutor {
    private Points points;

    public ShameCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"30%"},
            usage = "30%",
            privateMessages = false
    )

    public void onCommand(String bettingPoints, ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 2)) {
            serverTextChannel.sendMessage("https://gist.github.com/Mazawrath/ce6dec5784b9bb85ad38bf372569ffa8");
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
    }
}
