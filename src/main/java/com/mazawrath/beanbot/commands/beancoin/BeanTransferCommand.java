package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

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
                if (StringUtils.isNumeric(args[1])) {
                    if (!args[1].equals("0")) {
                        if (Integer.parseInt(args[1]) != 0) {
                            if (points.removePointsExcludeBeanbot(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[1]))) {
                                points.addPoints(user.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[1]));
                                serverTextChannel.sendMessage("Sent " + args[1] + " beanCoin to " + user.getDisplayName(server) + ".");
                            } else
                                serverTextChannel.sendMessage("You do not have enough beanCoin to send that much.");
                        }
                    } else
                        serverTextChannel.sendMessage("You can't give someone 0 beanCoin!");
                } else
                    serverTextChannel.sendMessage("Invalid amount of beanCoin.");
            });
        } else
            serverTextChannel.sendMessage("Not enough arguments.");
    }
}
