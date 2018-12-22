package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.javacord.api.DiscordApi;

public class StreamNotifier {
    private static DiscordApi api;

    private static Connection conn;

    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_NAME = "beanBotTwitch";
    private static final String TABLE_NAME = "AdminTable";

    public static void setApi(DiscordApi api) {
        StreamNotifier.api = api;
    }

    public static void setConn(Connection conn) {
        StreamNotifier.conn = conn;
    }

    public static void notifyLive(LivestreamNotification livestreamNotification) {
        String id = livestreamNotification.getUserName();

//        api.getServerById(ids[0]).ifPresent(server -> {
//            server.getTextChannelById(ids[1]).ifPresent(serverTextChannel -> {
//                serverTextChannel.sendMessage("They went live horray");
//            });
//        });
    }
}
