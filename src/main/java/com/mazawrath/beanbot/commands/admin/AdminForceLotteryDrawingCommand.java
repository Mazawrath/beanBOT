package com.mazawrath.beanbot.commands.admin;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AdminForceLotteryDrawingCommand implements CommandExecutor {
    private Points points;
    private Lottery lottery;

    public AdminForceLotteryDrawingCommand(Points points, Lottery lottery) {
        this.points = points;
        this.lottery = lottery;
    }

    @Command(
            aliases = {"adminforcelotterydrawing"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        if (!author.isBotOwner() && !server.isOwner(author)) {
            // There is no better var name than this and if you think otherwise you're wrong.
            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
            return;
        }

        int[] winningNumbers = lottery.getWinningNumbers();

        ArrayList winners = lottery.getWinner(server.getIdAsString(), winningNumbers);
        MessageBuilder message = new MessageBuilder();

        message.append("The numbers drawn were:\n");
        for (int i = 0; i < winningNumbers.length; i++)
            message.append(winningNumbers[i] + " ");
        message.append("\n");
        if (winners.size() == 0)
            message.append("No one has won.");
        else {
            BigDecimal prizePool = points.getBalance(api.getYourself().getIdAsString(), server.getIdAsString());
            BigDecimal amountWon = prizePool.divide(new BigDecimal(winners.size())).setScale(Points.SCALE, Points.ROUNDING_MODE);
            points.removePoints(api.getYourself().getIdAsString(), null, server.getIdAsString(), points.getBalance(api.getYourself().getIdAsString(), server.getIdAsString()));

            winners.forEach(winner -> points.addPoints(((HashMap) winner).get("id").toString(), server.getIdAsString(), amountWon));

            message.append("The following users have won:\n");
            winners.forEach(winner ->
                    api.getCachedUserById((((HashMap) winner).get("id").toString())).ifPresent(user ->
                            message.append(user.getMentionTag() + " has won!\n")));

            message.append("The prize pool was " + Points.pointsToString(prizePool) + " and divided between " + winners.size());
            if (winners.size() == 1)
                message.append(" winner, they get the entire prize pool!");
            else
                message.append(" winners, each gets " + Points.pointsToString(amountWon) + "!");
            lottery.clearTickets(server.getIdAsString());
        }
        message.send(serverTextChannel);
    }
}
