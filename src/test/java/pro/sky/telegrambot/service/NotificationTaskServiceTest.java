package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationTaskServiceTest {

    @Mock
    private NotificationTaskRepository notificationTaskRepository;

    @InjectMocks
    private NotificationTaskService notificationTaskService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void createTask_shouldSaveTaskAndReturnSuccessMessage_whenTextIsValidAndDateIsInFuture() {
        Long chatId = 123L;
        LocalDateTime sendAt = LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0);
        String datePart = sendAt.format(FORMATTER);
        String messagePart = "Сделать домашнюю работу";
        String text = datePart + " " + messagePart;

        SendMessage result = notificationTaskService.createTask(chatId, text);

        ArgumentCaptor<NotificationTask> captor = ArgumentCaptor.forClass(NotificationTask.class);
        verify(notificationTaskRepository).save(captor.capture());

        NotificationTask savedTask = captor.getValue();
        assertThat(savedTask.getChatId()).isEqualTo(chatId);
        assertThat(savedTask.getSendAt()).isEqualTo(sendAt);
        assertThat(savedTask.getMessage()).isEqualTo(messagePart);

        assertThat(result.getParameters().get("chat_id").toString()).isEqualTo(chatId.toString());
        assertThat(result.getParameters().get("text").toString())
                .contains("Создано напоминание")
                .contains(messagePart)
                .contains(datePart);
    }

    @Test
    void createTask_shouldReturnUnknownMessageAndNotSave_whenTextHasInvalidFormat() {
        Long chatId = 123L;
        String text = "невалидный формат";

        SendMessage result = notificationTaskService.createTask(chatId, text);

        verify(notificationTaskRepository, never()).save(any(NotificationTask.class));
        assertThat(result.getParameters().get("chat_id").toString()).isEqualTo(chatId.toString());
        assertThat(result.getParameters().get("text").toString())
                .contains("Я не понимаю");
    }

    @Test
    void createTask_shouldReturnPastDateMessageAndNotSave_whenDateIsInPast() {
        Long chatId = 123L;
        LocalDateTime sendAt = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
        String datePart = sendAt.format(FORMATTER);
        String text = datePart + " Проверить старую задачу";

        SendMessage result = notificationTaskService.createTask(chatId, text);

        verify(notificationTaskRepository, never()).save(any(NotificationTask.class));
        assertThat(result.getParameters().get("chat_id").toString()).isEqualTo(chatId.toString());
        assertThat(result.getParameters().get("text").toString())
                .contains("в прошлом")
                .contains(datePart);
    }

    @Test
    void findDueTasks_shouldDelegateToRepository() {
        LocalDateTime now = LocalDateTime.now();
        List<NotificationTask> expected = List.of(NotificationTask.builder().id(1L).build());

        when(notificationTaskRepository.findBySendAtLessThanEqualAndSentFalse(now)).thenReturn(expected);

        List<NotificationTask> actual = notificationTaskService.findDueTasks(now);

        assertThat(actual).isEqualTo(expected);
        verify(notificationTaskRepository).findBySendAtLessThanEqualAndSentFalse(now);
    }

    @Test
    void markAsSentById_shouldDelegateToRepository() {
        notificationTaskService.markAsSentById(42L);

        verify(notificationTaskRepository).markAsSentById(42L);
    }
}