package com.mazawrath.beanbot.commands;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UserInfoCommand implements CommandExecutor {
    private Points points;

    public UserInfoCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"userinfo"},
            usage = "userinfo [discriminated name]",
            description = "DO NOT @ USER. Enter user's discriminated name (Example#XXXX). Pulls up information about a user on the server. Leave username blank to look up yourself.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        final String[] roleList = {""};
        final String[] playing = {""};
        final String[] userName = new String[1];

        if (args.length != 0) {
            if (args[0].contains("@")) {
                if (args[0].contains("!"))
                    userName[0] = args[0].substring(3, args[0].length() - 1);
                else
                    userName[0] = args[0].substring(2, args[0].length() - 1);
            } else if (args[0].contains("#")) {
                api.getCachedUserByDiscriminatedNameIgnoreCase(args[0]).ifPresent(user -> userName[0] = user.getIdAsString());
            } else {
                serverTextChannel.sendMessage("Username is not valid!");
                return;
            }
        } else
            userName[0] = author.getIdAsString();
        api.getUserById(userName[0]).thenAccept(user -> {
            if (user.getRoles(server).get(0).getName().equals("@everyone") && user.getRoles(server).size() == 1) {
                roleList[0] = roleList[0] + "None";
            } else {
                for (int i = 1; i < user.getRoles(server).size(); i++) {
                    if (i != user.getRoles(server).size() - 1) {
                        roleList[0] = roleList[0] + user.getRoles(server).get(i).getName() + ", ";
                    } else
                        roleList[0] = roleList[0] + user.getRoles(server).get(i).getName();
                }
            }
//            user.getActivity().ifPresent(activity -> {
//                if (user.getActivity().isPresent()) {
//                    if (activity.getName().equalsIgnoreCase("Spotify")) {
//                        playing[0] = "Listening to " + activity.getDetails().get();
//                    } else {
//                        playing[0] = "Playing " + activity.getName();
//                    }
//                }
//            });
//            if (!user.getActivity().isPresent()) {
//                playing[0] = "Currently " + user.getStatus().getStatusString();
//            }

            Date date = new Date(user.getJoinedAtTimestamp(server).get().getEpochSecond() * 1000L);
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a");
            format.setTimeZone(TimeZone.getTimeZone("EST"));
            String formatted = format.format(date);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(user.getDisplayName(server))
                    .setDescription(playing[0])
                    .setThumbnail(user.getAvatar())
                    .addInlineField("Username", user.getDiscriminatedName())
                    .addInlineField("Roles", roleList[0])
                    .addInlineField("Date Joined", formatted)
                    .addInlineField("beanCoin Balance", Points.pointsToString(points.getBalance(user.getIdAsString(), server.getIdAsString())))
                    .setFooter("User ID: " + user.getIdAsString());
            serverTextChannel.sendMessage(embed);
        });

        Sentry.clearContext();
    }

}
