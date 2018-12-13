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
            if (!args[0].contains("#")) {
                serverTextChannel.sendMessage("Username is not valid!");
                args[0] = "null#000000000000";
            } else if (args[0].contains("@")) {
                serverTextChannel.sendMessage("Do not mention the user, put in their full username (Example#0000) without a '@' in front.");
                args[0] = "null#000000000000";
            }
            api.getCachedUserByDiscriminatedNameIgnoreCase(args[0]).ifPresent(user -> {
                if (Points.isProperDecimal(args[1])) {
                    BigDecimal transferPoints = new BigDecimal(args[1]).setScale(Points.SCALE, Points.ROUNDING_MODE);
                    if (transferPoints.compareTo(BigDecimal.ZERO) != 0) {
                        int transfer = points.removeBeanmas(author.getIdAsString(), "", server.getIdAsString(), transferPoints);
                        if (transfer == 1) {
                            points.addPoints(user.getIdAsString(), server.getIdAsString(), transferPoints);
                            serverTextChannel.sendMessage("Sent " + Points.pointsToString(transferPoints) + " to " + user.getDisplayName(server) + " using your Beanmas balance! You have " + Points.pointsToString(points.getBeanmasBalance(author.getIdAsString(), server.getIdAsString())) + " left of your Beanmas balance.");
                        } else if (transfer == 0) {
                            points.addPoints(user.getIdAsString(), server.getIdAsString(), transferPoints);
                            serverTextChannel.sendMessage("Sent " + Points.pointsToString(transferPoints) + " to " + user.getDisplayName(server) + ". Your Beanmas balance is gone, but your regular balance was used.");
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
