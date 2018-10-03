package com.mazawrath.beanbot.commands.beanmarket;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.StockMarket;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class BeanInvestCommand implements CommandExecutor {
    private Points points;
    private StockMarket stockMarket;

    public BeanInvestCommand(Points points, StockMarket stockMarket) {
        this.points = points;
        this.stockMarket = stockMarket;
    }

    @Command(
            aliases = {"beanInvest", "coinInvest"},
            description = "Lets you invest in companies from the Bean Market.",
            usage = "beanInvest [[buy/sell] [symbol]]",
            privateMessages = false
    )

    public void onCommand(String[] args, ServerTextChannel serverTextChannel, User author, Server server) {
        if (args.length == 0) {
            serverTextChannel.sendMessage("No arguments for this command are currently not supported. In the future this will be a way to look at your portfolio and all the shares you have invested in.\n" +
                    "Instructions for `.beaninvest`.\n" +
                    "\t- `.beaninvest buy [symbol] [amount]` - Buys shares from that symbol.\n" +
                    "\t- `.beaninvest sell [symbol]` - Sells all shares bought from that symbol.");
        } else if (args[0].equals("buy")) {
            if (points.removePointsExcludeBeanbot(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[2]))) {
                stockMarket.buyShares(author.getIdAsString(), server.getIdAsString(), args[1], Integer.parseInt(args[2]));
            }
        } else if (args[0].equals("sell")) {
            // TODO make selling shares
            if (points.removePointsExcludeBeanbot(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[1]))) {
                stockMarket.buyShares(author.getIdAsString(), server.getIdAsString(), args[0], Integer.parseInt(args[1]));
            }
        }
    }
}
