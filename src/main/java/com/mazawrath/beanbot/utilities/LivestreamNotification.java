package com.mazawrath.beanbot.utilities;

import java.net.URL;

public class LivestreamNotification {
    private String userId;
    private String userName;
    private String title;
    private String gameId;
    private String thumbnail;

    public LivestreamNotification(String userId, String userName, String title, String gameId, String thumbnail) {
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.gameId = gameId;
        this.thumbnail = thumbnail.replace("{width}x{height}", "1920x1080");
    }

    public String getUserId() {
        return userId;
    }

    String getUserName() {
        return userName;
    }

    public String getTitle() {
        return title;
    }

    String getGameId() {
        return gameId;
    }

    String getThumbnail() {
        return thumbnail;
    }
}
