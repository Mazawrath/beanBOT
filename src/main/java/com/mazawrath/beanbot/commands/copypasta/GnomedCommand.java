package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class GnomedCommand implements CommandExecutor {
    private Points points;

    public GnomedCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"gnomed", "banuser", "everyone", "beanuser"},
            usage = "givemod",
            description = "Rolls a 10,000 sided die to randomly give mod.",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(null, author, server);

        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            serverTextChannel.sendMessage("```                __\n" +
                    "             .-'  |\n" +
                    "            /   <\\|     'Ello me ol' chum\n" +
                    "           /     \\'\n" +
                    "           |_.- o-o      I'm g'not a g'nelf\n" +
                    "           / C  -._)\\\n" +
                    "          /',        |   I'm g'not a g'noblin\n" +
                    "         |   `-,_,__,'\n" +
                    "         (,,)====[_]=|   I'm a g'nome and you've been GNOMED\n" +
                    "           '.   ____/\n" +
                    "            | -|-|_\n" +
                    "            |____)_)```");
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");

        Sentry.clearContext();
    }
}
