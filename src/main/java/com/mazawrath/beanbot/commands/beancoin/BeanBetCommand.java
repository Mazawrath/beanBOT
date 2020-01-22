package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.Breadcrumb;
import io.sentry.event.BreadcrumbBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.util.Random;

public class BeanBetCommand implements CommandExecutor {
    private Points points;

    public BeanBetCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanbet", "coinbet"},
            usage = "beanbet [amount]",
            description = "Earn a maximum amount beanCoin by participating in the server.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        if (args.length != 0) {
            if (Points.isProperDecimal(args[0])) {
                BigDecimal winningPoints = new BigDecimal(args[0]).setScale(Points.SCALE, Points.ROUNDING_MODE);
                if (winningPoints.compareTo(BigDecimal.ZERO) == 0) {
                    serverTextChannel.sendMessage("You can't bet 0 beanCoin!");
                } else {
                    if (points.removePoints(author.getIdAsString(), null, server.getIdAsString(), winningPoints)) {
                        Random rand = new Random();
                        int winningChance = rand.nextInt(100) + 1;

                        if (winningChance <= 45) {
                            int winningMultiplier = rand.nextInt(100) + 1;

                            if (winningMultiplier <= 15) {
                                winningPoints = winningPoints.multiply(new BigDecimal(3).setScale(Points.SCALE, Points.ROUNDING_MODE));
                                points.addPoints(author.getIdAsString(), server.getIdAsString(), winningPoints);
                                serverTextChannel.sendMessage("Congrats, you got the x3 multiplier! You won " + Points.pointsToString(winningPoints) + "!");
                                Sentry.getContext().recordBreadcrumb(
                                        new BreadcrumbBuilder()
                                                .setMessage("User won x3")
                                                .setLevel(Breadcrumb.Level.INFO).build()
                                );
                            } else {
                                winningPoints = winningPoints.multiply(new BigDecimal(2).setScale(Points.SCALE, Points.ROUNDING_MODE));
                                points.addPoints(author.getIdAsString(), server.getIdAsString(), winningPoints);
                                serverTextChannel.sendMessage("Congrats, you won " + Points.pointsToString(winningPoints) + "!");
                                Sentry.getContext().recordBreadcrumb(
                                        new BreadcrumbBuilder()
                                                .setMessage("User won x2")
                                                .setLevel(Breadcrumb.Level.INFO).build()
                                );
                            }
                        } else {
                            serverTextChannel.sendMessage("Sorry, you lost " + Points.pointsToString(winningPoints) + ".");
                            points.addPoints(api.getYourself().getIdAsString(), server.getIdAsString(), winningPoints);
                            Sentry.getContext().recordBreadcrumb(
                                    new BreadcrumbBuilder()
                                            .setMessage("User lost")
                                            .setLevel(Breadcrumb.Level.INFO).build()
                            );
                        }
                    } else
                        serverTextChannel.sendMessage("You don't have enough beanCoin to bet that much.");
                    Sentry.getContext().recordBreadcrumb(
                            new BreadcrumbBuilder()
                                    .setMessage("User doesn't have enough")
                                    .setLevel(Breadcrumb.Level.INFO).build()
                    );
                }
            } else
                serverTextChannel.sendMessage("Invalid amount of beanCoin.");
        }

        Sentry.clearContext();
    }
}
