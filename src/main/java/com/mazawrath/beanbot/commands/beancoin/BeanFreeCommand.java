package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class BeanFreeCommand implements CommandExecutor {
    private Points points;

    public BeanFreeCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanfree", "coinfree"},
            usage = "beanfree",
            description = "Get 25 beanCoin every 24 hours.",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.giveFreePoints(author.getIdAsString(), server.getIdAsString())) {
            serverTextChannel.sendMessage("You have received 25 beanCoin.");
        } else
            serverTextChannel.sendMessage("You can only receive free beanCoin every 24 hours.");
    }
}
