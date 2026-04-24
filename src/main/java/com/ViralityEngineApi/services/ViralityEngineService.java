package com.ViralityEngineApi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ViralityEngineService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final int BOT_REPLY_POINTS = 1;
    private static final int HUMAN_LIKE_POINTS = 20;
    private static final int HUMAN_COMMENT_POINTS = 50;

    private static final int MAX_BOT_REPLIES_PER_POST = 100;
    private static final int MAX_COMMENT_DEPTH = 20;
    private static final int BOT_HUMAN_COOLDOWN_MINUTES = 10;

    private static final String VIRALITY_SCORE_KEY = "post:%d:virality_score";
    private static final String BOT_COUNT_KEY = "post:%d:bot_count";
    private static final String COOLDOWN_KEY = "cooldown:bot_%d:human_%d";
    private static final String NOTIFICATION_COOLDOWN_KEY = "notif_cooldown:user_%d";
    private static final String PENDING_NOTIFICATIONS_KEY = "user:%d:pending_notifs";

    public void incrementViralityScore(Long postId, String interactionType) {
        String key = String.format(VIRALITY_SCORE_KEY, postId);
        int points = switch (interactionType.toLowerCase()) {
            case "bot_reply" -> BOT_REPLY_POINTS;
            case "human_like" -> HUMAN_LIKE_POINTS;
            case "human_comment" -> HUMAN_COMMENT_POINTS;
            default -> 0;
        };
        
        if (points > 0) {
            redisTemplate.opsForValue().increment(key, points);
        }
    }

    public boolean checkAndIncrementBotCount(Long postId) {
        String key = String.format(BOT_COUNT_KEY, postId);
        Long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount > MAX_BOT_REPLIES_PER_POST) {
            redisTemplate.opsForValue().increment(key, -1);
            return false;
        }
        
        return true;
    }

    public boolean checkCommentDepth(Integer depthLevel) {
        return depthLevel <= MAX_COMMENT_DEPTH;
    }

    public boolean checkAndSetBotHumanCooldown(Long botId, Long humanId) {
        String key = String.format(COOLDOWN_KEY, botId, humanId);
        
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "active", 
                Duration.ofMinutes(BOT_HUMAN_COOLDOWN_MINUTES));
        
        return Boolean.TRUE.equals(success);
    }

    public boolean checkNotificationCooldown(Long userId, String notificationMessage) {
        String cooldownKey = String.format(NOTIFICATION_COOLDOWN_KEY, userId);
        String pendingKey = String.format(PENDING_NOTIFICATIONS_KEY, userId);
        
        Boolean canSendImmediately = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "active", 
                Duration.ofMinutes(15));
        
        if (Boolean.TRUE.equals(canSendImmediately)) {
            System.out.println("Push Notification Sent to User " + userId);
            return true;
        } else {
            redisTemplate.opsForList().rightPush(pendingKey, notificationMessage);
            return false;
        }
    }

    public Long getViralityScore(Long postId) {
        String key = String.format(VIRALITY_SCORE_KEY, postId);
        Object score = redisTemplate.opsForValue().get(key);
        return score != null ? Long.parseLong(score.toString()) : 0L;
    }

    public Long getBotCount(Long postId) {
        String key = String.format(BOT_COUNT_KEY, postId);
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count.toString()) : 0L;
    }

    public java.util.List<String> getPendingNotifications(Long userId) {
        String pendingKey = String.format(PENDING_NOTIFICATIONS_KEY, userId);
        return redisTemplate.opsForList().range(pendingKey, 0, -1)
                .stream()
                .map(Object::toString)
                .toList();
    }

    public void clearPendingNotifications(Long userId) {
        String pendingKey = String.format(PENDING_NOTIFICATIONS_KEY, userId);
        redisTemplate.delete(pendingKey);
    }
}
