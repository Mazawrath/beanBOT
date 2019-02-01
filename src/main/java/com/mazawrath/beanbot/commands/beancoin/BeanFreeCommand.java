package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanFreeCommand implements CommandExecutor {
    private Points points;

    public BeanFreeCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanfree", "coinfree"},
            usage = "beanfree",
            description = "Get 25 beanCoin every 24 hours.",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(null, author, server);

        long timeLeft = points.giveFreePoints(null, server.getIdAsString());

        if (timeLeft == 0) {
            serverTextChannel.sendMessage("You have received " + Points.pointsToString(Points.FREE_POINTS) + ". You now have " + Points.pointsToString(points.getBalance(author.getIdAsString(), server.getIdAsString())) + ".");
        } else {
            StringBuilder message = new StringBuilder();

            message.append("You have already received free beanCoin today. You can receive beanCoin in ");

            String dateStart = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(System.currentTimeMillis()));
            String dateStop = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(timeLeft + 24 * 60 * 60 * 1000));

            //HH converts hour in 24 hours format (0-23), day calculation
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            Date d1;
            Date d2;

            try {
                d1 = format.parse(dateStart);
                d2 = format.parse(dateStop);

                //in milliseconds
                long diff = d2.getTime() - d1.getTime();

                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;

                if (diffHours == 1)
                    message.append(diffHours).append(" hour ");
                else
                    message.append(diffHours).append(" hours ");
                if (diffMinutes == 1)
                    message.append(diffMinutes).append(" minute.");
                else
                    message.append(diffMinutes).append(" minutes.");

                serverTextChannel.sendMessage(message.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Sentry.clearContext();
    }
}
