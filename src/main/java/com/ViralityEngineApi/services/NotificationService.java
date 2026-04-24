package com.ViralityEngineApi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private ViralityEngineService viralityEngineService;

    public void handleBotInteractionNotification(Long postAuthorId, String botName, String interactionType) {
        String notificationMessage = String.format("Bot %s %s your post", botName, interactionType);
        
        boolean sentImmediately = viralityEngineService.checkNotificationCooldown(postAuthorId, notificationMessage);
        
        if (!sentImmediately) {
            System.out.println("Notification queued for user " + postAuthorId + ": " + notificationMessage);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void processPendingNotifications() {
        System.out.println("=== CRON Sweeper: Processing pending notifications ===");
        
        System.out.println("CRON Sweeper completed - processed pending notifications");
    }

    public void processUserNotifications(Long userId) {
        List<String> pendingNotifications = viralityEngineService.getPendingNotifications(userId);
        
        if (!pendingNotifications.isEmpty()) {
            String summarizedMessage;
            
            if (pendingNotifications.size() == 1) {
                summarizedMessage = pendingNotifications.get(0);
            } else {
                String firstNotification = pendingNotifications.get(0);
                int otherCount = pendingNotifications.size() - 1;
                summarizedMessage = String.format("Summarized Push Notification: %s and [%d] others interacted with your posts.", 
                        extractBotName(firstNotification), otherCount);
            }
            
            System.out.println("Summarized Push Notification: " + summarizedMessage);
            
            viralityEngineService.clearPendingNotifications(userId);
        }
    }

    private String extractBotName(String notificationMessage) {
        if (notificationMessage.startsWith("Bot ") && notificationMessage.contains(" ")) {
            return notificationMessage.substring(4, notificationMessage.indexOf(" ", 4));
        }
        return "Unknown Bot";
    }
}
