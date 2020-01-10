package com.mazawrath.beanbot.utilities;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class PointsUser {
    private User user;
    private Server server;
    private String userId;
    private String serverId;

    public PointsUser(User user, Server server) {
        this.user = user;
        this.server = server;

        userId = user.getIdAsString();
        serverId = server.getIdAsString();
    }

    public String getUserId() {
        return userId;
    }

    public String getServerId() {
        return serverId;
    }
}
