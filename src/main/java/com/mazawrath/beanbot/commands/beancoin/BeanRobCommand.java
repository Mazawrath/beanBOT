package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.util.Random;

public class BeanRobCommand implements CommandExecutor {
    private Points points;

    public BeanRobCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanrob", "coinrob"},
            usage = "beanrob [discriminated name] [amount]",
            description = "Attempt to steal beanCoin from another user.",
            privateMessages = false
    )

    public void onCommand(String[] args, ServerTextChannel serverTextChannel, DiscordApi api, User author, Server server) {
        if (args.length < 2) {
            serverTextChannel.sendMessage("Not enough arguments");
            return;
        }
        if (!args[0].contains("#")) {
            serverTextChannel.sendMessage("Username is not valid!");
            return;
        } else if (args[0].contains("@")) {
            serverTextChannel.sendMessage("Do not mention the user, put in their full username (Example#0000) without a '@' in front.");
            return;
        }
        api.getCachedUserByDiscriminatedNameIgnoreCase(args[0]).ifPresent(user -> {
            BigDecimal stealPoints = new BigDecimal(args[1]).setScale(Points.SCALE, Points.ROUNDING_MODE);
            if (!Points.isProperDecimal(args[1])) {
                serverTextChannel.sendMessage("Invalid amount of beanCoin");
                return;
            }
            if (user.getIdAsString().equals(author.getIdAsString())) {
                serverTextChannel.sendMessage("You can't steal from yourself!");
                return;
            }
            if (stealPoints.compareTo(BigDecimal.ZERO) == 0) {
                serverTextChannel.sendMessage("You can't steal 0 beanCoin!");
                return;
            }
            if (points.getBalance(user.getIdAsString(), server.toString()).compareTo(stealPoints) < 0) {
                serverTextChannel.sendMessage(user.getDisplayName(server) + " does not have enough beanCoin!");
                return;
            }

            BigDecimal stealChance = stealPoints.divide(points.getBalance(user.getIdAsString(), server.toString())).setScale(Points.SCALE, Points.ROUNDING_MODE);
            BigDecimal stealChanceWanted = stealChance.add(points.getWantedLevel(author.getIdAsString(), server.toString()));

            if (stealChanceWanted.compareTo(new BigDecimal(90)) > 0)
                stealChanceWanted = new BigDecimal(90);

            Random rand = new Random();
            BigDecimal n = new BigDecimal(rand.nextInt(90) + 1);

            if (stealChanceWanted.compareTo(n) < 0) {
                points.removePoints(user.getIdAsString(), null, server.toString(), stealPoints);
                points.addPoints(user.getIdAsString(), server.toString(), stealPoints);

                points.addWantedLevel(author.getIdAsString(), server.toString(), stealChance);

                serverTextChannel.sendMessage("stealPoints" + stealPoints);
                serverTextChannel.sendMessage("stealchance" + stealChance);
                serverTextChannel.sendMessage("stealcgabcewanted" + stealChanceWanted);
            } else
                serverTextChannel.sendMessage("you failed go to bean jail");
        });
    }
}
