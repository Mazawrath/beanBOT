package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.net.MalformedURLException;
import java.net.URL;

public class LossCommand implements CommandExecutor {
    private Points points;

    public LossCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"loss"},
            usage = "loss",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(null, author, server);

        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            try {
                new MessageBuilder()
                        .addAttachment(new URL("https://cdn.discordapp.com/attachments/480959729330290688/489630723146514437/loss.png"))
                        .send(serverTextChannel);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");

        Sentry.clearContext();
    }
}
