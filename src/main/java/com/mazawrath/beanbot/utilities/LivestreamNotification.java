package com.mazawrath.beanbot.utilities;

import java.util.Random;

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
        this.thumbnail = thumbnail.replace("{width}x{height}", "1920x1080") + "?rnd=" + randomPasswordGenerator(6);
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
}
