package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationsSchedulerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private NotificationTaskService notificationTaskService;

    @InjectMocks
    private NotificationsScheduler notificationsScheduler;

    @Test
    void sendNotifications_shouldSendAndMarkAsSent_forEveryDueTask() {
        NotificationTask first = NotificationTask.builder()
                .id(1L)
                .chatId(100L)
                .sendAt(LocalDateTime.now().minusMinutes(1))
                .message("first")
                .build();

        NotificationTask second = NotificationTask.builder()
                .id(2L)
                .chatId(200L)
                .sendAt(LocalDateTime.now().minusMinutes(2))
                .message("second")
                .build();

        when(notificationTaskService.findDueTasks(any(LocalDateTime.class)))
                .thenReturn(List.of(first, second));

        notificationsScheduler.sendNotifications();

        verify(telegramBot, times(2)).execute(any(SendMessage.class));
        verify(notificationTaskService).markAsSentById(1L);
        verify(notificationTaskService).markAsSentById(2L);
    }

    @Test
    void sendNotifications_shouldNotMarkAsSent_whenTelegramSendingFails() {
        NotificationTask task = NotificationTask.builder()
                .id(1L)
                .chatId(100L)
                .sendAt(LocalDateTime.now().minusMinutes(1))
                .message("first")
                .build();

        when(notificationTaskService.findDueTasks(any(LocalDateTime.class)))
                .thenReturn(List.of(task));

        doThrow(new RuntimeException("telegram failed"))
                .when(telegramBot).execute(any(SendMessage.class));

        notificationsScheduler.sendNotifications();

        verify(notificationTaskService, never()).markAsSentById(1L);
    }

    @Test
    void sendNotifications_shouldMarkAsSentOnlyAfterSuccessfulSend() {
        NotificationTask task = NotificationTask.builder()
                .id(7L)
                .chatId(100L)
                .sendAt(LocalDateTime.now().minusMinutes(1))
                .message("ordered")
                .build();

        when(notificationTaskService.findDueTasks(any(LocalDateTime.class)))
                .thenReturn(List.of(task));

        notificationsScheduler.sendNotifications();

        InOrder inOrder = inOrder(telegramBot, notificationTaskService);
        inOrder.verify(telegramBot).execute(any(SendMessage.class));
        inOrder.verify(notificationTaskService).markAsSentById(7L);
    }
}