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

    public void onCommand(String command, String userName, String transferAmount, ServerTextChannel serverTextChannel, DiscordApi api, User author, Server server) {
        if (userName != null) {
            if (!userName.contains("#")) {
                serverTextChannel.sendMessage("Username is not valid!");
                userName = "null#000000000000";
            } else if (userName.contains("@")) {
                serverTextChannel.sendMessage("Do not mention the user, put in their full username (Example#0000) without a '@' in front.");
                userName = "null#000000000000";
            }
            api.getCachedUserByDiscriminatedNameIgnoreCase(userName).ifPresent(user -> {
                if (StringUtils.isNumeric(transferAmount)) {
                    if (!transferAmount.equals("0")) {
                        if (Integer.parseInt(transferAmount) != 0) {
                            if (points.removePointsExcludeBeanbot(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(transferAmount))) {
                                points.addPoints(user.getIdAsString(), server.getIdAsString(), Integer.parseInt(transferAmount));
                                serverTextChannel.sendMessage("Sent " + transferAmount + " beanCoin to " + user.getDisplayName(server) + ".");
                            } else
                                serverTextChannel.sendMessage("You do not have enough beanCoin to send that much.");
                        }
                    } else
                        serverTextChannel.sendMessage("You can't give someone 0 beanCoin!");
                } else
                    serverTextChannel.sendMessage("Invalid amount of beanCoin.");
            });
        } else
            serverTextChannel.sendMessage("Username is not valid!");
    }
}
