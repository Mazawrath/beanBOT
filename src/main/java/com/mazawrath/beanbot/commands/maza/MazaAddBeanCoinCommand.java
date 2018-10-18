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

    public void onCommand(String command, String user, String pointValue, ServerTextChannel serverTextChannel, User author, Server server) {
        if (author.isBotOwner()) {
			if (Points.isProperDecimal(pointValue)) {
				BigDecimal transferPoints = new BigDecimal(pointValue).setScale(Points.SCALE, Points.ROUNDING_MODE);
				points.addPoints(user, server.getIdAsString(), transferPoints);
				serverTextChannel.sendMessage("Added " + Points.pointsToString(transferPoints) + " to " + user + ".");
			} else
				serverTextChannel.sendMessage("Invalid amount of beanCoin.");
        }
        else
            serverTextChannel.sendMessage("Only Mazawrath can send this message.");
    }
}
