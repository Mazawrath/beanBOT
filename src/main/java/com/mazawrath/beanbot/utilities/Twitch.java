package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.TwitchClientBuilder;
import org.apache.http.HttpResponse;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.toilelibre.libe.curl.Curl.curl;

public class Twitch {
    private TwitchClient client;
    private String clientId;
    private String ipAddresss;
    private static DiscordApi api;

    private static Connection conn;

    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_NAME = "beanBotTwitch";
    private static final String TABLE_NAME = "AdminTable";

    public Twitch(String clientId, String ipAddress, Connection conn) {
        this.clientId = clientId;
        this.ipAddresss = ipAddress;
        this.conn = conn;

        checkTable(conn);
        startResubscribeTimer();
    }

    private void checkTable(Connection conn) {
        if (r.dbList().contains(DB_NAME).run(conn)) {
        } else {
            r.dbCreate(DB_NAME).run(conn);
            r.db(DB_NAME).tableCreate(TABLE_NAME).run(conn);
        }
    }

    private void checkServer(String serverID) {
        if (r.db(DB_NAME).table(TABLE_NAME).getField("id").contains(serverID).run(conn)) {
        } else
            r.db(DB_NAME).table(TABLE_NAME).insert(r.array(
                    r.hashMap("id", serverID)
            )).run(conn);
    }

    public boolean addServer(String user, String serverId, String channelId) {
        checkServer(serverId);
        boolean retVal = false;
        long userId = getUserID(user);

        if (userId != -1) {
            r.db(DB_NAME).table(TABLE_NAME).filter(r.array(
                    r.hashMap("id", serverId))).update(
                    r.hashMap("userId", userId)).run(conn);
            r.db(DB_NAME).table(TABLE_NAME).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("channelId", channelId)).run(conn);
            r.db(DB_NAME).table(TABLE_NAME).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("delete_requested", false)).run(conn);
            if (subscribeToLiveNotfications(userId))
                retVal = true;
            else {
                System.out.println("Subscription attempt failed. Retrying.");
                for (int attempts = 1; attempts <= 3; attempts++) {
                    try {
                        System.out.println("Attempt " + attempts);
                        Thread.sleep(5000);
                        if (subscribeToLiveNotfications(userId)) {
                            retVal = true;
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return retVal;
    }

    public boolean removeServer(String user, String serverId) {
        checkServer(serverId);
        boolean retVal = false;
        long userId = getUserID(user);

        if (userId != -1) {
            r.db(DB_NAME).table(TABLE_NAME).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("delete_requested", true)).run(conn);
            if (unsubscribeFromLiveNotfications(userId)) {
                retVal = true;
                r.db(DB_NAME).table(TABLE_NAME).filter(r.array(
                        r.hashMap("id", serverId))).delete().run(conn);
            }
        }
        return retVal;
    }

    public void setChannel(String serverId, String channelId) {
        // TODO I need to make super special checks for setting notifcation channel
        checkServer(serverId);

        r.db(DB_NAME).table(TABLE_NAME).filter(r.array(
                r.hashMap("id", serverId))).update(r.array(
                r.hashMap("channelId", channelId))).run(conn);
    }

    private long[] getChannelSubsciptionList() {
        return r.db(DB_NAME).table(TABLE_NAME).getField("userId").run(conn);
    }

    public long[] getServers(String userId) {
        return r.db(DB_NAME).table(TABLE_NAME).filter(r.array(r.hashMap("userId", userId))).getField(r.array("serverId", "channelId")).run(conn);
    }

    public void connectClient(String id, String secret, String credential) {
        client = TwitchClientBuilder.init()
                .withClientId(id)
                .withClientSecret(secret)
                .withAutoSaveConfiguration(true)
                .withConfigurationDirectory(new File("config"))
                .withCredential(credential) // Get your token at: https://twitchapps.com/tmi/
                .connect();
    }

    public boolean checkIfLive(String channel) {
        return client.getStreamEndpoint().isLive(client.getChannelEndpoint().getChannel(channel));
    }

    public long getUserID(String user) {
        HttpResponse response = curl("-H 'Client-ID: " + clientId + "' https://api.twitch.tv/helix/users?login=" + user);
        long retVal = -1;

        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader streamReader;
            try {
                streamReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                if (jsonObject.getJSONArray("data").length() != 0)
                    retVal = jsonObject.getJSONArray("data").getJSONObject(0).getInt("id");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    public static void notifyLive(LivestreamNotification livestreamNotification) {
        String[] servers = r.db(DB_NAME).table(TABLE_NAME).filter(r.hashMap("userId", livestreamNotification.getUserId())).getField("id").run(conn);
        String[] channels = r.db(DB_NAME).table(TABLE_NAME).filter(r.hashMap("userId", livestreamNotification.getUserId())).getField("channelId").run(conn);

        System.out.println("Notifying channel...");

        for(int i = 0; i < servers.length; i++) {
            int finalI = i;
            api.getServerById(servers[i]).ifPresent(server -> {
                server.getTextChannelById(channels[finalI]).ifPresent(serverTextChannel -> {
                    serverTextChannel.sendMessage(livestreamNotification.getUserName() + " has gone live!");
                });
            });
        }
    }

    private boolean subscribeToLiveNotfications(long userId) {
        //TODO replace secret with secure way of making password
        return curl("-H 'Client-ID: " + clientId + "' -H 'Content-Type: application/json' -X POST -d " +
                "'{\"hub.mode\":\"subscribe\", \"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id=" + userId + "\"," +
                " \"hub.callback\":\"http://" + ipAddresss + ":8081/api/twitchapi/subscription\", \"hub.lease_seconds\":\"864000\", \"hub.secret\":\"very_secret\"}'" +
                " https://api.twitch.tv/helix/webhooks/hub").getStatusLine().getStatusCode() == 202;
    }

    private boolean unsubscribeFromLiveNotfications(long userId) {
        //TODO replace secret with secure way of making password

        return curl("-H 'Client-ID: " + clientId + "' -H 'Content-Type: application/json' -X POST -d " +
                "'{\"hub.mode\":\"unsubscribe\", \"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id=" + userId + "\"," +
                " \"hub.callback\":\"http://" + ipAddresss + ":8081/api/twitchapi/subscription\", \"hub.lease_seconds\":\"864000\", \"hub.secret\":\"very_secret\"}'" +
                " https://api.twitch.tv/helix/webhooks/hub").getStatusLine().getStatusCode() == 202;
    }

    private void startResubscribeTimer() {
        ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);
        stpe.scheduleAtFixedRate(new resubscribe(), 0, 7, TimeUnit.DAYS);
    }

    class resubscribe implements Runnable {
        public void run() {
            long[] userIds = getChannelSubsciptionList();

            for (int i = 0; i < userIds.length; i++)
                subscribeToLiveNotfications(userIds[i]);
        }
    }
}
