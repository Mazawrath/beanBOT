package com.mazawrath.beanbot.commands.beanmarket;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.StockMarket;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;

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
            if (args.length >= 3) {
                if (points.removePoints(author.getIdAsString(), null, server.getIdAsString(), new BigDecimal(args[2]).setScale(Points.SCALE, Points.ROUNDING_MODE))) {
                    BigDecimal sharesBought = stockMarket.buyShares(author.getIdAsString(), server.getIdAsString(), args[1].toUpperCase(), new BigDecimal(args[2]).setScale(Points.SCALE, Points.ROUNDING_MODE));
                    if (sharesBought.compareTo(new BigDecimal(0)) > 0)
                        serverTextChannel.sendMessage("Bought " + sharesBought + " shares from " + stockMarket.getComapanyName(args[1].toUpperCase()));
                    else
                        serverTextChannel.sendMessage("Symbol not found.");
                } else
                    serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
            } else
                serverTextChannel.sendMessage("Not enough arguments.");
        } else if (args[0].equals("sell")) {
            // TODO make selling shares
            stockMarket.sellShares(author.getIdAsString(), server.getIdAsString(), args[0]);
        }
    }
}
