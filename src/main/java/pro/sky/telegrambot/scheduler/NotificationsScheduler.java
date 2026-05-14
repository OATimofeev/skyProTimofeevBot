package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.provider.SendMessageProvider;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;

@Component
@Slf4j
public class NotificationsScheduler {

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskService service;

    @Scheduled(cron = "0 * * * * *")
    public void sendNotifications() {
        service.getNotificationTaskById(LocalDateTime.now()).forEach(x ->
        {
            try {
                telegramBot
                        .execute(SendMessageProvider.notifyMessage(x));
                service
                        .markAsSentById(x.getId());
            } catch (Exception e) {
                log.error("Error with processing send notification: {}, chatId {}, taskId {}", e, x.getChatId(), x.getId());
            }
        });
    }
}
