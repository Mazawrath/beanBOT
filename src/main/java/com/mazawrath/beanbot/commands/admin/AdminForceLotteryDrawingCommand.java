package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.Lottery;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AdminForceLotteryDrawingCommand implements CommandExecutor {
    private Points points;
    private Lottery lottery;

    public AdminForceLotteryDrawingCommand(Points points, Lottery lottery) {
        this.points = points;
        this.lottery = lottery;
    }

    @Command(
            aliases = {"adminforcelotterydrawing"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        SentryLog.addContext(null, author, server);

        if (!author.isBotOwner() && !server.isOwner(author)) {
            // There is no better var name than this and if you think otherwise you're wrong.
//            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
            return;
        }

        lottery.drawNumbers(points, server, api, serverTextChannel);

        Sentry.clearContext();
    }
}
