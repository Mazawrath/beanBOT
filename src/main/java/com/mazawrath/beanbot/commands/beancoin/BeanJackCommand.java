package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.mazawrath.beanbot.utilities.black_jack.Blackjack;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import io.sentry.event.Breadcrumb;
import io.sentry.event.BreadcrumbBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeanJackCommand implements CommandExecutor {
    private Points points;

    public BeanJackCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanjack", "coinjack"},
            usage = "beanbet [amount]",
            description = "Bet beanCoin to either win or lose.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        new Thread(() -> {
            Blackjack game = new Blackjack(author);


        }).start();


        Sentry.clearContext();
    }
}
