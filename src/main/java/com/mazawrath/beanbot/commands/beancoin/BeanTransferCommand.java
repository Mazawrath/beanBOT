package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.math.MathContext;

public class BeanTransferCommand implements CommandExecutor {
    private Points points;

    public BeanTransferCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beantransfer", "cointransfer"},
            usage = "beantransfer [discriminated name] [amount]",
            description = "Sends beanCoin to another user.",
            privateMessages = false
    )

    public void onCommand(String[] args, ServerTextChannel serverTextChannel, DiscordApi api, User author, Server server) {
        if (args.length >= 2) {
            if (args[0].contains("@")) {
                args[0] = args[0].substring(2,args[0].length() - 1);
            } else if (args[0].contains("#")) {
                api.getCachedUserByDiscriminatedNameIgnoreCase(args[0]).ifPresent(user -> args[0] = user.getIdAsString());
            } else {
                serverTextChannel.sendMessage("Username is not valid!");
                return;
            }

            api.getUserById(args[0]).thenAccept(user -> {
                if (Points.isProperDecimal(args[1])) {
                    BigDecimal transferPoints = new BigDecimal(args[1]).setScale(Points.SCALE, Points.ROUNDING_MODE);
                    if (transferPoints.compareTo(BigDecimal.ZERO) != 0) {
                        if (points.removePoints(author.getIdAsString(), "", server.getIdAsString(), transferPoints)) {
                            points.addPoints(user.getIdAsString(), server.getIdAsString(), transferPoints);
                            serverTextChannel.sendMessage("Sent " + Points.pointsToString(transferPoints) + " to " + user.getDisplayName(server) + ".");
                        } else
                            serverTextChannel.sendMessage("You do not have enough beanCoin to send that much.");
                    } else
                        serverTextChannel.sendMessage("You can't give someone 0 beanCoin!");
                } else
                    serverTextChannel.sendMessage("Invalid amount of beanCoin.");
            });
        } else
            serverTextChannel.sendMessage("Not enough arguments.");
    }
}
