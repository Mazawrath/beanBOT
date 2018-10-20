package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public class BeanBetCommand implements CommandExecutor {
    private Points points;

    public BeanBetCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanbet", "coinbet"},
            usage = "beanbet [amount]",
            description = "Bet beanCoin to either win or lose.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        if (args.length != 0) {
            if (Points.isProperDecimal(args[0])) {
                BigDecimal winningPoints = new BigDecimal(args[0]).setScale(Points.SCALE, Points.ROUNDING_MODE);
                if (winningPoints.compareTo(BigDecimal.ZERO) == 0) {
                    serverTextChannel.sendMessage("You can't bet 0 beanCoin!");
                } else {
                    if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), winningPoints)) {
                        Random rand = new Random();
                        int winningChance = rand.nextInt(100) + 1;

                        if (winningChance <= 35) {
                            int winningMultiplier = rand.nextInt(100) + 1;

                            if (winningMultiplier <= 15) {
                                winningPoints = winningPoints.multiply(new BigDecimal(3).setScale(Points.SCALE, Points.ROUNDING_MODE));
                                points.addPoints(author.getIdAsString(), server.getIdAsString(), winningPoints);
                                serverTextChannel.sendMessage("Congrats, you got the x3 multiplier! You won " + Points.pointsToString(winningPoints) + "!");
                            } else {
                                winningPoints = winningPoints.multiply(new BigDecimal(2).setScale(Points.SCALE, Points.ROUNDING_MODE));
                                points.addPoints(author.getIdAsString(), server.getIdAsString(), winningPoints);
                                serverTextChannel.sendMessage("Congrats, you won " + Points.pointsToString(winningPoints) + "!");
                            }
                        } else {
                            serverTextChannel.sendMessage("Sorry, you lost " + Points.pointsToString(winningPoints) + ".");
                        }
                    } else
                        serverTextChannel.sendMessage("You don't have enough beanCoin to bet that much.");
                }
            } else
                serverTextChannel.sendMessage("Invalid amount of beanCoin.");
        } else
            serverTextChannel.sendMessage("Invalid amount of beanCoin.");
    }
}
