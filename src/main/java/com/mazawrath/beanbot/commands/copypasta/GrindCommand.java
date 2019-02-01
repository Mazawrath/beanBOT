package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import static java.nio.charset.StandardCharsets.*;

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

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(null, author, server);

        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            serverTextChannel.sendMessage("Respect the grind bro, I am live all day every day and can\u0027t get views but I love what I do, I\u0027m live rn but hopefully I get some people, I\u0027m playing fort :) Comgrats on ur success");
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");

        Sentry.clearContext();
    }
}
