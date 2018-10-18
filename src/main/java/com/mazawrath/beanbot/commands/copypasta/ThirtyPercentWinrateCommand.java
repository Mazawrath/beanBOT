package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class ThirtyPercentWinrateCommand implements CommandExecutor {
    private Points points;

    public ThirtyPercentWinrateCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"30%"},
            usage = "30%",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            serverTextChannel.sendMessage("shteebs 30% winrate roadhog is uncarriable. I got 3 losses off him the other night. Now if I get him I just instalock hog so I have a chance of winning.");
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
    }
}
