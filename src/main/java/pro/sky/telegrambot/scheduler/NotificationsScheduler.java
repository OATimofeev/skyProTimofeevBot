package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.provider.SendMessageProvider;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationsScheduler {

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    @Scheduled(
            cron = "${app.notifications.cron}",
            zone = "${app.notifications.zone}"
    )
    public void sendNotifications() {
        notificationTaskService.findDueTasks(LocalDateTime.now()).forEach(x ->
        {
            try {
                telegramBot
                        .execute(SendMessageProvider.notifyMessage(x));
                notificationTaskService
                        .markAsSentById(x.getId());
            } catch (Exception e) {
                log.error("Error with processing send notification: chatId '{}', taskId '{}'. Stacktrace: ", x.getChatId(), x.getId(), e);
            }
        });
    }
}
