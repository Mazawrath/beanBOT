package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class AdminRemoveBeanCoinCommand implements CommandExecutor {
    private Points points;

    public AdminRemoveBeanCoinCommand(Points pointsPassed) {
        points = pointsPassed;
    }

    @Command(
            aliases = {"adminremovebeancoin"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        if (!author.isBotOwner()) {
            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " can use this command.");
            return;
        }

        if (Points.isProperDecimal(args[1])) {
            BigDecimal transferPoints = new BigDecimal(args[1]).setScale(Points.SCALE, Points.ROUNDING_MODE);
            if (points.removePoints(args[0], null, server.getIdAsString(), transferPoints))
                serverTextChannel.sendMessage("Removed " + Points.pointsToString(transferPoints) + " from " + args[0] + ".");
            else
                serverTextChannel.sendMessage("Attempted to remove " + Points.pointsToString(transferPoints) + " beanCoin from " + args[0] + " but the user does not have enough beanCoin.");
        } else
            serverTextChannel.sendMessage("Invalid amount of beanCoin.");
    }
}
