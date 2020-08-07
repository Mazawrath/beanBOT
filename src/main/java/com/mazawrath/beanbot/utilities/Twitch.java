package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import org.apache.http.HttpResponse;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.toilelibre.libe.curl.Curl.curl;

public class Twitch {
    private static String clientId;
    private static String clientSecret;
    private static String token;
    private String ipAddress;
    private static DiscordApi api;

    private static Connection conn;

    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_NAME = "beanBotTwitch";
    private static final String SERVER_SUBSCRIPTION_LIST_TABLE = "ServerSubscriptionList";
    private static final String TWITCH_CHANNEL_LIST_TABLE = "TwitchChannelList";

    public Twitch(String clientId, String ipAddress, String clientSecret, Connection conn) {
        Twitch.clientId = clientId;
        this.ipAddress = ipAddress;
        Twitch.clientSecret = clientSecret;
        Twitch.conn = conn;

        checkDatabase();
        checkTable();
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

    public static String getToken() {
        HttpResponse response = curl("-X POST https://id.twitch.tv/oauth2/token?client_id=" + clientId + "&grant_type=client_credentials&client_secret=" + clientSecret);
        token = "-1";

        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader streamReader;
            try {
                streamReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                if (jsonObject.getString("access_token").length() != 0)
                    token = jsonObject.getString("access_token");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return token;
    }

    public int addServer(String user, String serverId, String channelId) {
        int retVal = 0;
        String userId = String.valueOf(getUserID(user));

        if (!userId.equalsIgnoreCase("-1")) {
            if (r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).getAll(serverId).count().eq(1).run(conn))
                return -1;

            checkServer(serverId);
            checkTwitchId(Long.valueOf(userId));
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("userId", userId)).run(conn);
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("channelId", channelId)).run(conn);
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                    r.hashMap("id", serverId)).update(
                    r.hashMap("delete_requested", false)).run(conn);

            r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", Long.valueOf(userId))).update(r.hashMap("previously_live", false)).run(conn);
            r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", Long.valueOf(userId))).update(r.hashMap("previous_time_live", 0)).run(conn);
            if (subscribeToLiveNotifications(Long.parseLong(userId)))
                retVal = 1;
            else {
                System.out.println("Subscription attempt failed. Retrying.");
                for (int attempts = 1; attempts <= 3; attempts++) {
                    try {
                        System.out.println("Attempt " + attempts);
                        Thread.sleep(5000);
                        if (subscribeToLiveNotifications(Long.parseLong(userId))) {
                            retVal = 1;
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        retVal = 0;
                    }
                }
            }
        }
        return retVal;
    }

    public boolean removeServer(String serverId) {
        boolean retVal = false;

        Cursor cursor = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                r.hashMap("id", serverId)).getField("userId").run(conn);
        List userIdList = cursor.toList();

        if (userIdList.size() != 0) {
            long userId = Long.valueOf(userIdList.get(0).toString());

            if (unsubscribeFromLiveNotifications(userId))
                retVal = true;

            if (r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.hashMap("userId", String.valueOf(userId))).count().eq(1).run(conn)) {
            }
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.hashMap(
                    "id", serverId)).delete().run(conn);
        }
        return retVal;
    }

    public static void removeTwitchChannel(long userId) {
        r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).delete().run(conn);
    }

    public boolean setChannel(String serverId, String channelId) {
        boolean retVal = false;

        Cursor cursor = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(
                r.hashMap("id", serverId)).getField("userId").run(conn);
        List userIdList = cursor.toList();

        if (userIdList.size() != 0) {

            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array(
                    r.hashMap("id", serverId))).update(r.array(
                    r.hashMap("channelId", channelId))).run(conn);
            r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.hashMap("id", serverId)).update(r.hashMap("channelId", channelId)).run(conn);

            retVal = true;
        }
        return retVal;
    }

    private List getChannelSubsciptionList() {
        Cursor retVal = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).getField("id").run(conn);
        return retVal.toList();
    }

    public long[] getServers(String userId) {
        return r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.array(r.hashMap("userId", userId))).getField(r.array("serverId", "channelId")).run(conn);
    }

    public static long getUserID(String user) {
        HttpResponse response = curl("-H 'Client-ID: " + clientId + "' https://api.twitch.tv/helix/users?login=" + user);
        long retVal = -1;

        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader streamReader;
            try {
                streamReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
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

    public static String getUserName(long userId) {
        HttpResponse response = curl("-H 'Client-ID: " + clientId + "' https://api.twitch.tv/helix/users?id=" + userId);
        String retVal = null;

        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader streamReader;
            try {
                streamReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                if (jsonObject.getJSONArray("data").length() != 0)
                    retVal = jsonObject.getJSONArray("data").getJSONObject(0).getString("display_name");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    public static String getGameName(String gameId) {
        HttpResponse response = curl("-H 'Client-ID: " + clientId + "' https://api.twitch.tv/helix/games?id=" + gameId);
        String retVal = null;

        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader streamReader;
            try {
                streamReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                if (jsonObject.getJSONArray("data").length() != 0)
                    retVal = jsonObject.getJSONArray("data").getJSONObject(0).getString("name");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    public static void notifyLive(LivestreamNotification livestreamNotification) {
        Cursor serverCursor = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.hashMap("userId", livestreamNotification.getUserId())).getField("id").run(conn);
        List serverId = serverCursor.toList();

        Cursor channelJson = r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.hashMap("userId", livestreamNotification.getUserId())).getField("channelId").run(conn);
        List channelId = channelJson.toList();

        System.out.println(serverId.size());
        System.out.println(channelId.size());

        System.out.println("Notifying channel...");

        System.out.println(livestreamNotification.getThumbnail());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(livestreamNotification.getUserName())
                .setDescription("[" + livestreamNotification.getTitle() + "]" + "(https://www.twitch.tv/" + livestreamNotification.getUserName() + ")")
                .addInlineField("Viewer count", String.valueOf(livestreamNotification.getViewerCount()))
                .setImage(livestreamNotification.getThumbnail())
                .setFooter("Playing: " + Twitch.getGameName(livestreamNotification.getGameId()));

        MessageBuilder message = new MessageBuilder()
                .append("@everyone " + livestreamNotification.getUserName() + " has gone live!")
                .setEmbed(embed);


        for (int i = 0; i < serverId.size(); i++) {
            int finalI = i;
            if (checkServer(api, serverId.get(i).toString(), channelId.get(i).toString())) {
                System.out.println("Valid server.");
                api.getServerById(serverId.get(i).toString()).ifPresent(server ->
                        server.getTextChannelById(channelId.get(finalI).toString()).ifPresent(message::send));
            } else {
                System.out.println("Invalid server.");
                r.db(DB_NAME).table(SERVER_SUBSCRIPTION_LIST_TABLE).filter(r.hashMap(
                        "id", serverId.get(i).toString())).delete().run(conn);

                if (serverId.size() == 1)
                    Twitch.removeTwitchChannel(Long.valueOf(livestreamNotification.getUserId()));
            }
        }
    }

//    private static boolean checkPreviouslyLive(long userId) {
//        return r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("previously_live");
//    }

    private static boolean checkServer(DiscordApi api, String serverId, String textChannelId) {
        boolean retVal = false;
        if (api.getServerById(serverId).isPresent())
            if (api.getServerById(serverId).get().getTextChannelById(textChannelId).isPresent())
                retVal = true;
        return retVal;
    }

    private String checkHubSecret(long userId) {
        checkTwitchId(userId);

        Cursor passwordDb = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("secret").run(conn);

        List secretVar = passwordDb.toList();
        if (secretVar.isEmpty()) {
            String password = randomPasswordGenerator(24);
            r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).update(r.hashMap("secret", password)).run(conn);
            return password;
        } else
            return secretVar.get(0).toString();
    }

    public static String getHubSecret(long userId) {
        Cursor passwordDb = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("secret").run(conn);
        List password = passwordDb.toList();

        return password.size() == 0 ? null : password.get(0).toString();
    }

    public static boolean getStatus(long userId) {
        Cursor dbReturn = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("previously_live").run(conn);
        
        return ((boolean) dbReturn.toList().get(0));
    }

    public static void setStatus(long userId, boolean status) {
        r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).update(r.hashMap("previously_live", status)).run(conn);
    }

    public static long getOfflineTime(long userId) {
        Cursor dbReturn = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("previous_time_live").run(conn);
        List offlineTime = dbReturn.toList();

        return (long) offlineTime.get(0);
    }

    public static void setOfflineTime(long userId) {
        r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).update(r.hashMap("previous_time_live", System.currentTimeMillis())).run(conn);
    }

    private String checkPassword(long userId) {
        checkTwitchId(userId);

        Cursor passwordDb = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("password").run(conn);

        List passwordVar = passwordDb.toList();
        if (passwordVar.isEmpty()) {
            String password = randomPasswordGenerator(5);
            r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).update(r.hashMap("password", password)).run(conn);
            return password;
        } else
            return passwordVar.get(0).toString();
    }

    public static String getPassword(long userId) {
        Cursor passwordDb = r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).filter(r.hashMap("id", userId)).getField("password").run(conn);
        List password = passwordDb.toList();

        return password.size() == 0 ? null : password.get(0).toString();
    }

    private void checkTwitchId(long userId) {
        if (r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).getField("id").contains(userId).run(conn)) {
        } else
            r.db(DB_NAME).table(TWITCH_CHANNEL_LIST_TABLE).insert(r.hashMap("id", userId
            )).run(conn);
    }

    private boolean subscribeToLiveNotifications(long userId) {
        return curl("-H 'Client-ID: " + clientId + "' -H 'Content-Type: application/json' -X POST -d " +
                "'{\"hub.mode\":\"subscribe\", \"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id=" + userId + "\"," +
                " \"hub.callback\":\"http://" + ipAddress + ":8081/api/twitchapi/stream_changed?username=" + getUserName(userId) + "&password=" + checkPassword(userId) + "\"," +
                " \"hub.lease_seconds\":\"864000\", \"hub.secret\":\"" + checkHubSecret(userId) + "\"}'" + " https://api.twitch.tv/helix/webhooks/hub").getStatusLine().getStatusCode() == 202;
    }

    private boolean unsubscribeFromLiveNotifications(long userId) {
        return curl("-H 'Client-ID: " + clientId + "' -H 'Content-Type: application/json' -X POST -d " +
                "'{\"hub.mode\":\"unsubscribe\", \"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id=" + userId + "\"," +
                " \"hub.callback\":\"http://" + ipAddress + ":8081/api/twitchapi/stream_changed?username=" + getUserName(userId) + "&password=" + Twitch.getPassword(userId) + "\", \"hub.lease_seconds\":\"864000\", \"hub.secret\":\"" + Twitch.getHubSecret(userId) + "\"}'" +
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

    private String randomPasswordGenerator(int passwordLength) {
        // A strong password has Cap_chars, Lower_chars,
        // numeric value and symbols. So we are using all of
        // them to generate our password
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        //String symbols = "!@#$%^&*_=+-/.?<>)";


        String values = Capital_chars + Small_chars +
                numbers;

        // Using random method
        Random random_method = new Random();

        char[] password = new char[passwordLength];

        for (int i = 0; i < passwordLength; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            password[i] =
                    values.charAt(random_method.nextInt(values.length()));

        }
        return String.valueOf(password);
    }

    public static void setApi(DiscordApi api) {
        Twitch.api = api;

        //Twitch.notifyLive(new LivestreamNotification("403074652", "302985029385", "hey boys", "", "https://static-cdn.jtvnw.net/previews-ttv/live_user_302985029385-1920x1080.jpg"));
    }
}
