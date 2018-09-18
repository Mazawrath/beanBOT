package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class Top500Command implements CommandExecutor {
    private Points points;

    public Top500Command(Points pointsPassed) {
        points = pointsPassed;
    }

    @Command(
            aliases = {"top500"},
            usage = "top500",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 2)) {
            serverTextChannel.sendMessage("This is your friendly neighborhood Game Master Lograldon. I am sorry to hear that your account is not showing up on the top 500 leaderboards for " +
                    "Overwatch! I know how frustrating it can be when things in-game dont work like you expect >.<\n" +
                    "\n" +
                    "I can confirm that you activated SMS Protect on your account on August 11th, and all competitive games played since then have counted towards your ranking for top 500." +
                    " That being said, customer support does not have any visibility into the ranking system, so if you believe there is an error you will need to make a report on the Overwatch Bug Report forums.");
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
    }
}
