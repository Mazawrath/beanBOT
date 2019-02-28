package com.mazawrath.beanbot.commands.beanlottery;

import com.mazawrath.beanbot.utilities.Lottery;
import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.NonThrowingAutoCloseable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BeanLotteryCommand implements CommandExecutor {
    private Points points;
    private Lottery lottery;

    public BeanLotteryCommand(Points points, Lottery lottery) {
        this.points = points;
        this.lottery = lottery;
    }

    @Command(
            aliases = {"beanlottery"},
            usage = "beanlottery [amount of tickets to buy/ 4 numbers >= 1 and <= 20]",
            description = "Buys a lottery ticket, either input a number saying how many tickets you want or enter a set of 4 numbers to manually create a ticket.",
            privateMessages = false,
            async = true
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        try { // Thanks a lot Jeff
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (!author.isBotOwner() && !server.isOwner(author)) {
                        try {
                            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        return;
                    } else {
                        serverTextChannel.sendMessage("Automatic drawings now active. Drawing will happen at this time every 3 days.");
                        lottery.scheduleWeeklyDrawing(points, server, api, serverTextChannel);
                        return;
                    }
                } //else if (args[0].equalsIgnoreCase("draw")) {
//                    if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_DRAWING_COST)) {
//                        lottery.drawNumbers(points, server, api, serverTextChannel);
//                    } else
//                        serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
//                    return;
//                }
                if (Integer.parseInt(args[0]) > 200) {
                    serverTextChannel.sendMessage("You can only buy 200 tickets at a time.");
                    return;
                }

                if (Integer.parseInt(args[0]) < 1) {
                    serverTextChannel.sendMessage("You cannot buy less than 1 ticket.");
                    return;
                }

                if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_TICKET_COST.multiply(new BigDecimal(Integer.parseInt(args[0]))))) {
                    if (!lottery.canBuyTickets(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[0]))) {
                        serverTextChannel.sendMessage("You can only buy " + lottery.getMaxTickets(server.getIdAsString()) + " tickets at a time for a bean lottery drawing. You have bought " + lottery.getTicketCount(author.getIdAsString(), server.getIdAsString()) + " tickets.");
                        return;
                    }

                    ArrayList<ArrayList<Integer>> numbers;

                    try (NonThrowingAutoCloseable typingIndicator = serverTextChannel.typeContinuouslyAfter(1, TimeUnit.SECONDS)) {
                        numbers = lottery.addEntry(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[0]));

                        serverTextChannel.sendMessage(args[0] + " tickets bought.\n" +
                                "The numbers generated have been sent to you in a private message.");
                    }
                    author.sendMessage(args[0] + " tickets bought.\n" +
                            "Your numbers are:");
                    MessageBuilder message = new MessageBuilder();

                    for (int i = 0; i < numbers.size(); i++) {
                        for (int j = 0; j < Lottery.AMOUNT_DRAWN; j++)
                            message.append(numbers.get(i).get(j) + " ");
                        message.append("\n");
                    }
                    message.send(author);
                } else
                    serverTextChannel.sendMessage("You don't have enough beanCoin to buy that many tickets.");
            } else if (args.length >= Lottery.AMOUNT_DRAWN) {
                if (!lottery.canBuyTickets(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[0]))) {
                    serverTextChannel.sendMessage("You can only buy " + lottery.getMaxTickets(server.getIdAsString()) + " tickets at a time for a bean lottery drawing. You have bought " + lottery.getTicketCount(author.getIdAsString(), server.getIdAsString()) + " tickets.");
                    return;
                }

                if (!StringUtils.isNumeric(args[0])) {
                    serverTextChannel.sendMessage("Invalid amount.");
                    return;
                }

                if (!lottery.canBuyTickets(author.getIdAsString(), server.getIdAsString(), 1)) {
                    serverTextChannel.sendMessage("You can only buy " + lottery.getMaxTickets(server.getIdAsString()) + " tickets at the moment. After every drawing you will be able to buy " + Lottery.ADD_AFTER_DRAWING + " more tickets. You have bought " + lottery.getTicketCount(author.getIdAsString(), server.getIdAsString()) + " tickets.");
                    return;
                }

                int[] numbers = new int[Lottery.AMOUNT_DRAWN];

                for (int i = 0; i < Lottery.AMOUNT_DRAWN; i++) {
                    if (Integer.parseInt(args[i]) >= Lottery.MIN_NUMBER && Integer.parseInt(args[i]) <= Lottery.MAX_NUMBER)
                        numbers[i] = Integer.parseInt(args[i]);
                    else {
                        serverTextChannel.sendMessage(args[i] + " is an invalid number. Numbers must be greater than or equal to " + Lottery.MIN_NUMBER + " and less than or equal to " + Lottery.MAX_NUMBER);
                        return;
                    }
                }
                if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.LOTTERY_TICKET_COST)) {
                    lottery.addEntry(author.getIdAsString(), server.getIdAsString(), numbers);

                    serverTextChannel.sendMessage("1 ticket bought.\n" +
                            "Your numbers have been sent to you in a private message.");
                    author.sendMessage("1 ticket bought.\n" +
                            "Your numbers are:\n");
                    author.sendMessage(args[0] + " " + args[1] + " " + args[2]);
                } else
                    serverTextChannel.sendMessage("You do not have enough beanCoin to buy a ticket.");
            } else
                serverTextChannel.sendMessage("You must have 1 number with how many tickets you want to buy, " + Lottery.AMOUNT_DRAWN + " numbers >= " + Lottery.MIN_NUMBER + " and <= " + Lottery.MAX_NUMBER + ", or the word `draw` to have your own drawing.");
        } catch (NumberFormatException e) {
            serverTextChannel.sendMessage("Invalid number(s).");
        }
        Sentry.clearContext();
    }
}
