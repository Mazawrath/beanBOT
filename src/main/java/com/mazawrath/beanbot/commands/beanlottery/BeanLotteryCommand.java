package com.mazawrath.beanbot.commands.beanlottery;

import com.mazawrath.beanbot.utilities.Lottery;
import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.math.BigDecimal;

public class BeanLotteryCommand implements CommandExecutor {
    private Points points;
    private Lottery lottery;

    public BeanLotteryCommand(Points points, Lottery lottery) {
        this.points = points;
        this.lottery = lottery;
    }

    @Command(
            aliases = {"beanlottery"},
            usage = "beanlottery [amount of tickets to buy/numbers",
            description = "Buys a lottery ticket, either input a number saying how many tickets you want or enter a set of 4 numbers to manually create a ticket.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        try {
            if (args.length == 2) {
                if (!points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_TICKET_COST.multiply(new BigDecimal(Integer.parseInt(args[1]))))) {
                    serverTextChannel.sendMessage("You don't have enough beanCoin to buy that many tickets.");
                }
                lottery.addEntry(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[1]));
            } else if (args.length >= 5) {
                int[] numbers = new int[4];

                for (int i = 1; i < 4; i++) {
                    numbers[i] = Integer.parseInt(args[i]);
                }
                if (!points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_TICKET_COST)) {
                    lottery.addEntry(author.getIdAsString(), server.getIdAsString(), numbers);
                }
            }
        } catch (NumberFormatException | NullPointerException e) {
            serverTextChannel.sendMessage("Invalid amount of tickets.");
        }
    }
}
