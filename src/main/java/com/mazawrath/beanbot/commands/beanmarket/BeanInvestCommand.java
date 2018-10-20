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
                if (stockMarket.isProperDecimal(args[2])) {
                    if (StockMarket.getSymbol(args[1].toUpperCase()) != null) {
                        if (new BigDecimal(args[2]).divide(stockMarket.getStockPrice(StockMarket.getSymbol(args[1].toUpperCase()), true)).setScale(Points.SCALE, Points.ROUNDING_MODE).compareTo(BigDecimal.ZERO) < .01) {
                            if (points.removePoints(author.getIdAsString(), null, server.getIdAsString(), new BigDecimal(args[2]).setScale(Points.SCALE, Points.ROUNDING_MODE))) {
                                BigDecimal sharesBought = stockMarket.buyShares(author.getIdAsString(), server.getIdAsString(), args[1].toUpperCase(), new BigDecimal(args[2]).setScale(Points.SCALE, Points.ROUNDING_MODE));
                                if (sharesBought.compareTo(BigDecimal.ZERO) > 0)
                                    serverTextChannel.sendMessage("Bought " + sharesBought + " shares from " + stockMarket.getComapanyName(args[1].toUpperCase()));
                                else
                                    serverTextChannel.sendMessage("some error happened and I don't know what it is.");
                            } else
                                serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
                        } else
                            serverTextChannel.sendMessage("You must buy at least 1% of the share.");
                    } else
                        serverTextChannel.sendMessage("Symbol not found.");
                } else
                    serverTextChannel.sendMessage("Invalid amount of beanCoin");
            } else
                serverTextChannel.sendMessage("Not enough arguments.");
        } else if (args[0].equals("sell")) {
            if (args.length >= 2) {
                if (StockMarket.getSymbol(args[1].toUpperCase()) != null) {
                    BigDecimal[] amounts = stockMarket.sellShares(author.getIdAsString(), server.getIdAsString(), args[1].toUpperCase());
                    if (!amounts[0].equals(BigDecimal.ZERO)) {
                        BigDecimal outCome = amounts[0].multiply(stockMarket.getStockPrice(args[1].toUpperCase(), true));
                        StringBuilder message = new StringBuilder();

                        if (amounts[0].compareTo(BigDecimal.ZERO) > 0) {
                            message.append("You bought ").append(amounts[0]).append(" shares for ").append(stockMarket.pointsToString(amounts[1])).append(" with shares for ").append(stockMarket.getComapanyName(args[1].toUpperCase()))
                                    .append(" selling at ").append(stockMarket.pointsToString(stockMarket.getStockPrice(args[1].toUpperCase(), true))).append(" per share, you earned ")
                                    .append(stockMarket.pointsToString(amounts[0].multiply(stockMarket.getStockPrice(args[1].toUpperCase(), true)))).append(" from it and you got a ")
                                    .append(stockMarket.pointsToString(outCome.subtract((stockMarket.getStockPrice(args[1].toUpperCase(), true)))));

                            points.addPoints(author.getIdAsString(), server.getIdAsString(), outCome);

                            if (outCome.compareTo(amounts[1]) >= 0) {
                                message.append(" gain.");
                            } else
                                message.append(" loss.");
                        }
                        serverTextChannel.sendMessage(message.toString());
                    } else if (amounts[0].equals(new BigDecimal(-1))) {
                        serverTextChannel.sendMessage("Uh oh this bug shouldn't happen and I wouldn't be able to explain why.");
                    } else
                        serverTextChannel.sendMessage("You do not own any shares in this symbol.");
                } else
                    serverTextChannel.sendMessage("Symbol not found.");
            } else
                serverTextChannel.sendMessage("Not enough arguments");
        }
    }
}
