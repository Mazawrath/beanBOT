package com.mazawrath.beanbot.commands.beanlottery;

import com.mazawrath.beanbot.utilities.Lottery;
import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
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
            usage = "beanlottery [amount of tickets to buy/ 4 numbers > 0 and <= 40]",
            description = "Buys a lottery ticket, either input a number saying how many tickets you want or enter a set of 4 numbers to manually create a ticket.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        try {
            if (args.length == 1) {
                if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_TICKET_COST.multiply(new BigDecimal(Integer.parseInt(args[0]))))) {
                    int[][] numbers = lottery.addEntry(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[0]));

                    author.sendMessage(args[0] + " tickets bought.\n" +
                            "Your numbers are:");
                    MessageBuilder message = new MessageBuilder();

                    for (int i = 0; i < numbers.length; i++) {
                        for (int j = 0; j < 3; j++)
                            message.append(numbers[i][j] + " ");
                        message.append("\n");
                    }

                    message.send(author);
                } else
                    serverTextChannel.sendMessage("You don't have enough beanCoin to buy that many tickets.");
            } else if (args.length >= 3) {
                int[] numbers = new int[3];

                for (int i = 0; i < 3; i++) {
                    if (Integer.parseInt(args[i]) > 0 && Integer.parseInt(args[i]) <= 20)
                        numbers[i] = Integer.parseInt(args[i]);
                    else {
                        serverTextChannel.sendMessage(args[i] + " is an invalid number. Numbers must be greater than 0 and less than or equal to 20");
                        return;
                    }
                }
                if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_TICKET_COST)) {
                    lottery.addEntry(author.getIdAsString(), server.getIdAsString(), numbers);
                    author.sendMessage("1 ticket bought.\n" +
                            "Your numbers are:\n");
                            author.sendMessage(args[0] + " " + args[1] + " " + args[2]);
                }
            } else
                serverTextChannel.sendMessage("You must either have 1 number with how many tickets you want to buy or three numbers >0 and <=20.");
        } catch (NumberFormatException | NullPointerException e) {
            serverTextChannel.sendMessage("Invalid number(s).");
        }
    }
}
