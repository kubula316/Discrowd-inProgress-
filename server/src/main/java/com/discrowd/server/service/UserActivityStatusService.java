package com.discrowd.server.service;

import com.discrowd.server.config.RedisConfig;
import com.discrowd.server.model.status.UserActivityStatus;
import com.discrowd.server.model.status.UserStatusUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors; // Do filtrowania strumieni

@Service
@RequiredArgsConstructor
public class UserActivityStatusService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String REDIS_SERVER_USER_STATUS_PREFIX = "server:";
    private static final String REDIS_USER_STATUS_SUFFIX = ":user_statuses";
    private static final String REDIS_USER_CURRENT_SERVER_KEY_PREFIX = "user:";
    private static final String REDIS_USER_CURRENT_SERVER_KEY_SUFFIX = ":current_server";

    private String getServerStatusHashKey(String serverId) {
        return REDIS_SERVER_USER_STATUS_PREFIX + serverId + REDIS_USER_STATUS_SUFFIX;
    }

    private String getUserCurrentServerKey(String userId) {
        return REDIS_USER_CURRENT_SERVER_KEY_PREFIX + userId + REDIS_USER_CURRENT_SERVER_KEY_SUFFIX;
    }

    public void updateAndPublishUserStatus(String userId, UserActivityStatus status, String newServerId) {

        String currentServerIdInRedis = (String) redisTemplate.opsForValue().get(getUserCurrentServerKey(userId));

        // --- Obsługa statusu OFFLINE ---
        if (status == UserActivityStatus.OFFLINE) {
            String serverToClean = (newServerId != null && !newServerId.isEmpty()) ? newServerId : currentServerIdInRedis;

            if (serverToClean != null && !serverToClean.isEmpty()) {
                redisTemplate.opsForHash().delete(getServerStatusHashKey(serverToClean), userId);
                System.out.println("User " + userId + " (OFFLINE) removed from server " + serverToClean + " status hash.");

                // PUBLIKUJEMY BEZPOŚREDNIO DO STOMP (NIE PRZEZ REDIS PUB/SUB)
                UserStatusUpdate offlineStatus = new UserStatusUpdate(userId, UserActivityStatus.OFFLINE, serverToClean);
                String stompTopic = String.format("/topic/server/%s/status.updates", serverToClean);
                messagingTemplate.convertAndSend(stompTopic, offlineStatus);
                System.out.println("Published OFFLINE status to STOMP topic: " + stompTopic);
            }
            redisTemplate.delete(getUserCurrentServerKey(userId));
            System.out.println("User " + userId + " last known server cleared.");

        } else if (status == UserActivityStatus.ONLINE) {
            // --- Obsługa statusu ONLINE ---
            if (newServerId == null || newServerId.isEmpty()) {
                System.err.println("Attempted to set user " + userId + " to ONLINE without a newServerId. Ignoring.");
                return;
            }

            if (currentServerIdInRedis != null && !currentServerIdInRedis.isEmpty() && !currentServerIdInRedis.equals(newServerId)) {
                // Jeśli użytkownik był na innym serwerze, usuń go ze starego hasha w Redis
                redisTemplate.opsForHash().delete(getServerStatusHashKey(currentServerIdInRedis), userId);
                System.out.println("User " + userId + " moved from server " + currentServerIdInRedis + " to " + newServerId + ". Removed from old hash.");

                // PUBLIKUJEMY BEZPOŚREDNIO DO STOMP (OPUSZCZENIE STAREGO SERWERA)
                UserStatusUpdate oldServerOfflineStatus = new UserStatusUpdate(userId, UserActivityStatus.OFFLINE, currentServerIdInRedis);
                String oldStompTopic = String.format("/topic/server/%s/status.updates", currentServerIdInRedis);
                messagingTemplate.convertAndSend(oldStompTopic, oldServerOfflineStatus);
                System.out.println("Published OFFLINE status for old server to STOMP topic: " + oldStompTopic);
            }

            // Zapisz nowy status użytkownika w hashu dla NOWEGO serwera
            String newServerHashKey = getServerStatusHashKey(newServerId);
            UserStatusUpdate newStatus = new UserStatusUpdate(userId, UserActivityStatus.ONLINE, newServerId);
            redisTemplate.opsForHash().put(newServerHashKey, userId, newStatus);
            System.out.println("User " + userId + " status updated to ONLINE on server " + newServerId + " in Redis hash.");

            redisTemplate.opsForValue().set(getUserCurrentServerKey(userId), newServerId);
            System.out.println("User " + userId + " current server set to " + newServerId + ".");

            // PUBLIKUJEMY BEZPOŚREDNIO DO STOMP (DOŁĄCZENIE DO NOWEGO SERWERA)
            String newStompTopic = String.format("/topic/server/%s/status.updates", newServerId);
            messagingTemplate.convertAndSend(newStompTopic, newStatus);
            System.out.println("Published ONLINE status to STOMP topic: " + newStompTopic);
        }
    }

    public Map<String, UserStatusUpdate> getServerActiveUsers(String serverId) {
        String hashKey = getServerStatusHashKey(serverId);
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(hashKey);
        Map<String, UserStatusUpdate> serverUsers = new HashMap<>();

        rawMap.forEach((k, v) -> {
            if (k instanceof String userId && v instanceof UserStatusUpdate statusUpdate) {
                if (statusUpdate.getStatus() == UserActivityStatus.ONLINE) {
                    serverUsers.put(userId, statusUpdate);
                }
            }
        });
        System.out.println("Fetched initial active users for server " + serverId + ": " + serverUsers.size() + " users.");
        return serverUsers;
    }
}
