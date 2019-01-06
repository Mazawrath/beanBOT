package com.mazawrath.beanbot.utilities;

import java.net.URL;

public class LivestreamNotification {
    private String userId;
    private String userName;
    private String title;
    private String gameId;
    private int viewerCount;
    private String thumbnail;

    public LivestreamNotification(String userId, String userName, String title, String gameId, int viewerCount, String thumbnail) {
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.gameId = gameId;
        this.viewerCount = viewerCount;
        this.thumbnail = thumbnail.replace("{width}x{height}", "1920x1080");
    }

    String getUserId() {
        return userId;
    }

    String getUserName() {
        return userName;
    }

    String getTitle() {
        return title;
    }

    String getGameId() {
        return gameId;
    }

    int getViewerCount() {
        return viewerCount;
    }

    String getThumbnail() {
        return thumbnail;
    }
}
