package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class GrindCommand implements CommandExecutor {
    private Points points;

    public GrindCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"grind"},
            usage = "grind",
            privateMessages = false
    )

    public void onCommand(String bettingPoints, ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 2)) {
            serverTextChannel.sendMessage("Respect the grind bro, I am live all day every day and can’t get views but I love what I do, I’m live rn but hopefully I get some people, I’m playing fort :) Comgrats on ur success");
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
    }
}
