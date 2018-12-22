package com.mazawrath.beanbot.utilities;

import java.net.URL;

public class LivestreamNotification {
    private String userId;
    private String userName;
    private String gameId;
    private String thumbnail;

    public LivestreamNotification(String userId, String userName, String gameId, String thumbnail) {
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

    String getThumbnail() {
        return thumbnail;
    }
}
