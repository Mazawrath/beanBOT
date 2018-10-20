package com.mazawrath.beanbot.commands.beanmarket;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.StockMarket;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Bean Market Investment")
                    .setThumbnail("https://cdn.discordapp.com/attachments/489203676863397889/503334187621941308/Stock.png");
            if (args.length >= 3) {
                if (stockMarket.isProperDecimal(args[2])) {
                    if (StockMarket.getSymbol(args[1].toUpperCase()) != null) {
                        if (new BigDecimal(args[2]).setScale(Points.SCALE, Points.ROUNDING_MODE).divide(stockMarket.getStockPrice(StockMarket.getSymbol(args[1].toUpperCase()), false), 2, RoundingMode.HALF_UP).setScale(Points.SCALE, Points.ROUNDING_MODE).compareTo(new BigDecimal("0.01").setScale(Points.SCALE, Points.ROUNDING_MODE)) >= 0) {
                            BigDecimal roundedSharesBought = new BigDecimal(args[2]).divide(stockMarket.getStockPrice(args[1].toUpperCase(), true), 2, RoundingMode.DOWN);
                            BigDecimal stockPrice = stockMarket.getStockPrice(args[1].toUpperCase(), true);
                            BigDecimal beanCoinToSpend = roundedSharesBought.multiply(stockPrice);

                            if (points.removePoints(author.getIdAsString(), null, server.getIdAsString(), beanCoinToSpend)) {
                                if (stockMarket.buyShares(author.getIdAsString(), server.getIdAsString(), args[1].toUpperCase(), beanCoinToSpend).compareTo(BigDecimal.ZERO) > 0) {
                                    embed.setDescription("Buying Shares from " + stockMarket.getCompanyName(args[1].toUpperCase()));
                                    embed.addInlineField("beanCoin spent", stockMarket.pointsToString(beanCoinToSpend));
                                    embed.addInlineField("Shares bought", roundedSharesBought.toString() + " shares");
                                    embed.addInlineField("Shares you currently own", stockMarket.getShareInvested(author.getIdAsString(), server.getIdAsString(), StockMarket.getSymbol(args[1].toUpperCase())).toString() + " shares");
                                    embed.addInlineField("Current Portfolio Value", stockMarket.pointsToString(stockMarket.getShareInvested(author.getIdAsString(), server.getIdAsString(), StockMarket.getSymbol(args[1].toUpperCase())).multiply(stockPrice)));

                                    serverTextChannel.sendMessage(embed);
                                } else
                                    serverTextChannel.sendMessage("some error happened and I don't know what it is.");
                            } else
                                serverTextChannel.sendMessage("You do not have enough beanCoin for this command. You attempted to buy " + stockMarket.pointsToString(new BigDecimal(args[2])) + " worth of stocks but after rounding you were going to spend " + beanCoinToSpend + ".");
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
                            message.append("You bought ").append(amounts[0]).append(" shares for ").append(stockMarket.pointsToString(amounts[1])).append(" with shares for ").append(stockMarket.getCompanyName(args[1].toUpperCase()))
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
