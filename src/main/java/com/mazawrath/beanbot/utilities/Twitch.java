package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.GetField;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.TwitchClientBuilder;
import org.apache.http.HttpResponse;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.toilelibre.libe.curl.Curl.curl;

public class Twitch {
    private TwitchClient client;
    private String clientId;
    private String ipAddress;
    private static DiscordApi api;

    private static Connection conn;

    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_NAME = "beanBotTwitch";
    private static final String SERVER_SUBSCRIPTION_LIST_TABLE = "ServerSubscriptionList";
    private static final String TWITCH_CHANNEL_LIST_TABLE = "TwitchChannelList";

    public Twitch(String clientId, String ipAddress, Connection conn) {
        this.clientId = clientId;
        this.ipAddress = ipAddress;
        Twitch.conn = conn;

        checkTable(conn);
        startResubscribeTimer();
    }

    private void checkDatabase() {
        if (r.dbList().contains(DB_NAME).run(conn)) {
        } else {
            r.dbCreate(DB_NAME).run(conn);
        }
    }

    private void checkTable() {
        if (r.db(DB_NAME).tableList().contains(SERVER_SUBSCRIPTION_LIST_TABLE).run(conn)) {
        } else {
            r.db(DB_NAME).tableCreate(SERVER_SUBSCRIPTION_LIST_TABLE).run(conn);
        }
        
        if (r.db(DB_NAME).tableList().contains(TWITCH_CHANNEL_LIST_TABLE).run(conn)) {
        } else {
            r.db(DB_NAME).tableCreate(TWITCH_CHANNEL_LIST_TABLE).run(conn);
        }
    }

    private void checkServer(String serverID) {
        if (r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).getField("id").contains(serverID).run(conn)) {
        } else
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).insert(r.array(
                    r.hashMap("id", serverID)
            )).run(conn);
    }

    public boolean addServer(String user, String serverId, String channelId) {
        checkServer(serverId);
        boolean retVal = false;
        long userId = getUserID(user);

        if (userId != -1) {
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("userId", userId)).run(conn);
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("channelId", channelId)).run(conn);
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("delete_requested", false)).run(conn);
            if (subscribeToLiveNotifications(userId))
                retVal = true;
            else {
                System.out.println("Subscription attempt failed. Retrying.");
                for (int attempts = 1; attempts <= 3; attempts++) {
                    try {
                        System.out.println("Attempt " + attempts);
                        Thread.sleep(5000);
                        if (subscribeToLiveNotifications(userId)) {
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

    public boolean flagRemoval(String user, String serverId) {
        checkServer(serverId);
        boolean retVal = false;
        long userId = getUserID(user);

        if (userId != -1) {
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("delete_requested", true)).run(conn);
            if (unsubscribeFromLiveNotifications(userId))
                retVal = true;
        }
        return retVal;
    }

    public void setChannel(String serverId, String channelId) {
        // TODO I need to make super special checks for setting notifcation channel
        checkServer(serverId);

        r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array(
                r.hashMap("id", serverId))).update(r.array(
                r.hashMap("channelId", channelId))).run(conn);
    }

    private List getChannelSubsciptionList() {
        Cursor retVal = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).getField("userId").run(conn);
        return retVal.toList();
    }

    public long[] getServers(String userId) {
        return r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array(r.hashMap("userId", userId))).getField(r.array("serverId", "channelId")).run(conn);
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
        Cursor serverCursor = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array("userId", livestreamNotification.getUserId())).getField("id").run(conn);
        List serverId = serverCursor.toList();
        System.out.println(serverId.get(0).toString());

        Cursor channelJson = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array("userId", livestreamNotification.getUserId())).getField("channelId").run(conn);
        List channelId = channelJson.toList();
        System.out.println(channelId.get(0).toString());

        System.out.println("Notifying channel...");

        for (int i = 0; i < serverId.size(); i++) {
            int finalI = i;
            api.getServerById(serverId.get(i).toString()).ifPresent(server ->
                    server.getTextChannelById(channelId.get(finalI).toString()).ifPresent(serverTextChannel -> serverTextChannel.sendMessage(livestreamNotification.getUserName() + " has gone live!")));
        }
    }

    public static void removeServer(String serverId) {
        r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array(
                r.hashMap("id", serverId))).delete().run(conn);
    }

    private boolean subscribeToLiveNotifications(long userId) {
        //TODO replace secret with secure way of making password
        return curl("-H 'Client-ID: " + clientId + "' -H 'Content-Type: application/json' -X POST -d " +
                "'{\"hub.mode\":\"subscribe\", \"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id=" + userId + "\"," +
                " \"hub.callback\":\"http://" + ipAddress + ":8081/api/twitchapi/subscription\", \"hub.lease_seconds\":\"864000\", \"hub.secret\":\"very_secret\"}'" +
                " https://api.twitch.tv/helix/webhooks/hub").getStatusLine().getStatusCode() == 202;
    }

    private boolean unsubscribeFromLiveNotifications(long userId) {
        //TODO replace secret with secure way of making password

        return curl("-H 'Client-ID: " + clientId + "' -H 'Content-Type: application/json' -X POST -d " +
                "'{\"hub.mode\":\"unsubscribe\", \"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id=" + userId + "\"," +
                " \"hub.callback\":\"http://" + ipAddress + ":8081/api/twitchapi/subscription\", \"hub.lease_seconds\":\"864000\", \"hub.secret\":\"very_secret\"}'" +
                " https://api.twitch.tv/helix/webhooks/hub").getStatusLine().getStatusCode() == 202;
    }

    private void startResubscribeTimer() {
        ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);
        stpe.scheduleAtFixedRate(new resubscribe(), 0, 7, TimeUnit.DAYS);
    }

    class resubscribe implements Runnable {
        public void run() {
            List userIds = getChannelSubsciptionList();

            for (Object userId : userIds) subscribeToLiveNotifications(convertToLong(userId));
        }
    }

    private static Long convertToLong(Object o) {
        String stringToConvert = String.valueOf(o);
        return Long.parseLong(stringToConvert);

    }

    char[] randomPasswordGenerator()
    {
        System.out.println("Generating password using random() : ");
        System.out.print("Your new password is : ");

        // A strong password has Cap_chars, Lower_chars,
        // numeric value and symbols. So we are using all of
        // them to generate our password
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*_=+-/.?<>)";


        String values = Capital_chars + Small_chars +
                numbers + symbols;

        // Using random method
        Random rndm_method = new Random();

        int passwordLength = 10;
        char[] password = new char[passwordLength];

        for (int i = 0; i < passwordLength; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            password[i] =
                    values.charAt(rndm_method.nextInt(values.length()));

        }
        return password;
    }

    public static void setApi(DiscordApi api) {
        Twitch.api = api;
    }
}
