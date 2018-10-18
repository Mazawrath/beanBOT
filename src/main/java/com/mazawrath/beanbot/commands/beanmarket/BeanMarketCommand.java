package com.mazawrath.beanbot.commands.beanmarket;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.StockMarket;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.util.Arrays;

public class BeanMarketCommand implements CommandExecutor {
    @Command(
            aliases = {"beanmarket"},
            description = "Gets a status of the Bean Market. Leave argument blank for a list of every company",
            usage = "beanmarket [symbol]",
            privateMessages = false
    )

    public void onCommand(String[] args, ServerTextChannel serverTextChannel) {
        String[] companies = {"BEAN", "FBI", "SHTEB", "BEZFF", "ABD", "BNTC", "BETHS"};
        if (args.length == 0) {
            final String[] symbol = {""};
            final String[] price = {""};

            JSONArray array = new JSONArray(StockMarket.getCompanies(companies).toString());

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = new JSONObject(array.get(i).toString());
                symbol[0] += obj.getString("Symbol") + "\n";
                price[0] += Points.pointsToString(obj.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP)) + "\n";
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Bean Market")
                    .addInlineField("Symbol", symbol[0])
                    .addInlineField("Price", price[0])
                    //.setFooter("Use .beanmarket [Symbol] to look up a specific company. Investing coming soon!");
                    .setFooter("Use .beanmarket [Symbol] to look up a specific company. Use .beaninvest to check your portfolio.");
            serverTextChannel.sendMessage(embed);
        } else {
            if (Arrays.asList(companies).contains(args[0].toUpperCase())) {
                JSONArray array = new JSONArray(StockMarket.getCompanies(new String[]{args[0].toUpperCase()}).toString());

                if (!array.isNull(0)) {
                    JSONObject obj = new JSONObject(array.get(0).toString());

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(obj.getString("Name"))
                            .setDescription(obj.getString("Symbol"))
                            .setThumbnail(obj.getString("Logo"))
                            .addInlineField("Price", Points.pointsToString(obj.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP)))
                            .addInlineField("Daily Percentage Change", obj.getBigDecimal("Percentage Change").toString() + "%")
                            .addInlineField("Previously Closed At", Points.pointsToString(obj.getBigDecimal("Previous Close").setScale(2, RoundingMode.HALF_UP)))
                            .addInlineField("50 Day Percentage Change", obj.getBigDecimal("50 Day Percentage Change").setScale(2, RoundingMode.HALF_UP).toString() + "%")
                            .addInlineField("Yearly High", Points.pointsToString(obj.getBigDecimal("Year High").setScale(2, RoundingMode.HALF_UP)))
                            .addInlineField("Yearly Low", Points.pointsToString(obj.getBigDecimal("Year Low").setScale(2, RoundingMode.HALF_UP)));
                    serverTextChannel.sendMessage(embed);
                }
            } else
                serverTextChannel.sendMessage("Symbol not found.");
        }
    }

}
