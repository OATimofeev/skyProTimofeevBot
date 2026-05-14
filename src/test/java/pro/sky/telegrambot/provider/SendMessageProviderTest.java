package pro.sky.telegrambot.provider;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SendMessageProviderTest {

    @Test
    void notifyMessage_shouldReturnFormattedReminderText() {
        NotificationTask task = NotificationTask.builder()
                .id(1L)
                .chatId(123L)
                .sendAt(LocalDateTime.of(2026, 5, 14, 17, 47))
                .message("Сделать домашку")
                .build();

        SendMessage result = SendMessageProvider.notifyMessage(task);

        assertThat(result.getParameters().get("chat_id").toString()).isEqualTo("123");
        assertThat(result.getParameters().get("text").toString())
                .contains("Напоминаю!")
                .contains("14.05.2026 17:47")
                .contains("Сделать домашку")
                .doesNotContain("T17:47");
    }

    @Test
    void successCreatedTaskMessage_shouldContainDateAndMessage() {
        SendMessage result = SendMessageProvider.successCreatedTaskMessage(
                123L,
                "14.05.2026 17:47",
                "Сделать домашку"
        );

        assertThat(result.getParameters().get("chat_id").toString()).isEqualTo("123");
        assertThat(result.getParameters().get("text").toString())
                .contains("Создано напоминание")
                .contains("Сделать домашку")
                .contains("14.05.2026 17:47");
    }

    @Test
    void getDateInThePastMessage_shouldContainPastDateWarning() {
        SendMessage result = SendMessageProvider.getDateInThePastMessage(123L, "14.05.2020 17:47");

        assertThat(result.getParameters().get("chat_id").toString()).isEqualTo("123");
        assertThat(result.getParameters().get("text").toString())
                .contains("в прошлом")
                .contains("14.05.2020 17:47");
    }
}