package com.mazawrath.beanbot.utilities;

import java.net.URL;

public class LivestreamNotification {
    private String userId;
    private String userName;
    private String gameId;
    private URL thumbnail;

    public LivestreamNotification(String userId, String userName, String gameId, URL thumbnail) {
        this.userId = userId;
        this.userName = userName;
        this.gameId = gameId;
        this.thumbnail = thumbnail;
    }

    public String getUserId() {
        return userId;
    }

    String getUserName() {
        return userName;
    }

    String getGameId() {
        return gameId;
    }

    URL getThumbnail() {
        return thumbnail;
    }
}
