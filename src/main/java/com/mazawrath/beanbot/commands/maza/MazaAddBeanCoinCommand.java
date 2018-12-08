package com.mazawrath.beanbot.commands.maza;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.math.MathContext;

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

    public void onCommand(String[] args, ServerTextChannel serverTextChannel, User author, Server server) {
        if (!author.isBotOwner()) {
            serverTextChannel.sendMessage("Only Mazawrath can use this command.");
            return;
        }

        if (Points.isProperDecimal(args[1])) {
            BigDecimal transferPoints = new BigDecimal(args[1]).setScale(Points.SCALE, Points.ROUNDING_MODE);
            points.addPoints(args[0], server.getIdAsString(), transferPoints);
            serverTextChannel.sendMessage("Added " + Points.pointsToString(transferPoints) + " to " + args[0] + ".");
        } else
            serverTextChannel.sendMessage("Invalid amount of beanCoin.");
    }
}