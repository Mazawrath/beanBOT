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
import java.util.concurrent.TimeUnit;

public class BeanTalkCommand implements CommandExecutor {
    private Points points;

    public BeanTalkCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beantalk", "beanfree", "coinfree"},
            usage = "beantalk",
            description = "Get 25 beanCoin every 24 hours.",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(null, author, server);

        long[] beanTalkInfo = points.giveFreePoints(author.getIdAsString(), server.getIdAsString());
        long timeLeft = beanTalkInfo[0];

//        if (timeLeft == 0) {
//            serverTextChannel.sendMessage("You have received " + Points.pointsToString(Points.FREE_POINTS) + ". You now have " + Points.pointsToString(points.getBalance(author.getIdAsString(), server.getIdAsString())) + ".");
//        } else {
        StringBuilder message = new StringBuilder();

        message.append("You have already sent ").append(beanTalkInfo[1]).append(" messages. You can send up to ").append(Points.MAX_PARTICIPATION_MESSAGES).append(" messages. Your beanTalk participation counter will reset in ");

        String dateStart = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new java.util.Date(System.currentTimeMillis()));
        String dateStop = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new java.util.Date(timeLeft + Points.FREE_COIN_TIME_LIMIT));

        //HH converts hour in 24 hours format (0-23), day calculation
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            Date d1;
            Date d2;

            try {
                d1 = format.parse(dateStart);
                d2 = format.parse(dateStop);

                //in milliseconds
                long diff = d2.getTime() - d1.getTime();

                long days = TimeUnit.MILLISECONDS.toDays(diff);
                diff -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                diff -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                diff -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

                message.append(String.format("%d days, %d hours, %d mins, and %d seconds.",
                        days, hours, minutes, seconds));

                serverTextChannel.sendMessage(message.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
//        }

        Sentry.clearContext();
    }
}
