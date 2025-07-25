package com.discrowd.server.model.status;

import java.io.Serializable;

public class UserStatusUpdate implements Serializable {
    private String userId;
    private UserActivityStatus status;
    private String serverId;

    // WAŻNE: Wymagany konstruktor bezargumentowy dla Jacksona do deserializacji
    public UserStatusUpdate() {}

    public UserStatusUpdate(String userId, UserActivityStatus status, String serverId) {
        this.userId = userId;
        this.status = status;
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserActivityStatus getStatus() {
        return status;
    }

    public void setStatus(UserActivityStatus status) {
        this.status = status;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return "UserStatusUpdate{" +
                "userId='" + userId + '\'' +
                ", status=" + status +
                ", serverId='" + serverId + '\'' +
                '}';
    }
}
