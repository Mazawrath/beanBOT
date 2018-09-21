package com.mazawrath.beanbot.commands.beanmarket;

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

public class BeanMarketCommand implements CommandExecutor {
    @Command(
            aliases = {"beanmarket"},
            description = "Gets a status of the Bean Market. Leave command blank for a list of every company",
            usage = "beanbalance",
            privateMessages = false
    )

    public void onCommand(String[] command, ServerTextChannel serverTextChannel, User author, Server server) {
        if (command.length == 0) {
            final String[] symbol = {""};
            final String[] price = {""};

            JSONArray array = new JSONArray(StockMarket.getCompanies(new String[]{"BEAN", "FBI", "ATVI", "BEZFF", "ABD", "BNTC"}).toString());

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = new JSONObject(array.get(i).toString());
                symbol[0] += obj.getString("Symbol") + "\n";
                price[0] += "$ " + obj.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP) + "\n";
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Bean Market")
                    .addInlineField("Symbol", symbol[0])
                    .addInlineField("Price", price[0])
                    .setFooter("Use .beanmarket [Symbol] to look up a sepcific company. Use .beaninvest to check your portfolio.");
            serverTextChannel.sendMessage(embed);
        } else {
            JSONArray array = new JSONArray(StockMarket.getCompanies(new String[]{command[0].toUpperCase()}).toString());

            if (!array.isNull(0)) {
                JSONObject obj = new JSONObject(array.get(0).toString());

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(obj.getString("Name"))
                        .setDescription(obj.getString("Symbol"))
                        .setThumbnail(obj.getString("Logo"))
                        .addInlineField("Price", obj.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP).toString())
                        .addInlineField("Percentage Change", obj.getString("Percentage Change"))
                        .addInlineField("Opened at", obj.getBigDecimal("Opened").setScale(2, RoundingMode.HALF_UP).toString())
                        .addInlineField("Yearly High", obj.getBigDecimal("Year High").setScale(2, RoundingMode.HALF_UP).toString())
                        .addInlineField("Yearly Low", obj.getBigDecimal("Year Low").setScale(2, RoundingMode.HALF_UP).toString());
                serverTextChannel.sendMessage(embed);
            }
            else
                serverTextChannel.sendMessage("Symbol not found.");
        }
    }

}
