package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Random;

public class BeanBetCommand implements CommandExecutor {
    private Points points;

    public BeanBetCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanbet", "coinbet"},
            usage = "beanbet [amount]",
            description = "Bet beanCoin to either win or lose.",
            privateMessages = false
    )

    public void onCommand(String[] args, ServerTextChannel serverTextChannel, User author, Server server) {
        if (args.length != 0) {
            if (isInteger(args[0])) {
                if (args[0].equals("0")) {
                    serverTextChannel.sendMessage("You can't bet 0 beanCoin!");
                } else {
                    if (points.removePoints(author.getIdAsString(), server.getIdAsString(), Integer.parseInt(args[0]))) {
                        Random rand = new Random();
                        int winningChance = rand.nextInt(100) + 1;

                        if (winningChance <= 35) {
                            int winningMultiplier = rand.nextInt(100) + 1;

                            if (winningMultiplier <= 15) {
                                points.addPoints(author.getIdAsString(), server.getIdAsString(),
                                        (Integer.parseInt(args[0])) * 3);
                                serverTextChannel.sendMessage("Congrats, you got the x3 muliplayer! You won " + (Integer.parseInt(args[0]) * 3) + " beanCoin!");
                            } else {
                                points.addPoints(author.getIdAsString(), server.getIdAsString(),
                                        (Integer.parseInt(args[0])) * 2);
                                serverTextChannel.sendMessage("Congrats, you won " + (Integer.parseInt(args[0]) * 2) + " beanCoin!");
                            }
                        } else {
                            serverTextChannel.sendMessage("Sorry, you lost " + args[0] + " beanCoin.");
                        }
                    } else
                        serverTextChannel.sendMessage("You don't have enough beanCoin to bet that much.");
                }
            } else
                serverTextChannel.sendMessage("Invalid amount of beanCoin.");
        }else
            serverTextChannel.sendMessage("Invalid amount of beanCoin.");
    }

    private static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

}
