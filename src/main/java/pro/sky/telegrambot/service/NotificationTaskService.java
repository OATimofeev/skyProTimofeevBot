package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.constant.Constant;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.provider.SendMessageProvider;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public SendMessage createTask(Long chatId, String text) {
        log.info("Creating new task for chatId = '{}' with text = '{}'", chatId, text);
        Matcher matcher = Constant.MESSAGE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            log.error("Fail parse message '{}' from chatId = '{}'! We're not suppose to be here!!!", text, chatId);
            return SendMessageProvider.getDefaultUnknownMessage(chatId);
        }

        LocalDateTime sendAt = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        if (sendAt.isBefore(LocalDateTime.now())) {
            log.warn("Date '{}' is in the past! chatId = '{}'", matcher.group(1), chatId);
            return SendMessageProvider.getDateInThePastMessage(chatId, matcher.group(1));
        }
        notificationTaskRepository
                .save(NotificationTask
                        .builder()
                        .chatId(chatId)
                        .sendAt(sendAt)
                        .message(matcher.group(2))
                        .build());

        log.info("Created new task for chatId = '{}' with text = '{}'", chatId, text);
        return SendMessageProvider.successCreatedTaskMessage(chatId, matcher.group(1), matcher.group(2));
    }

    public List<NotificationTask> findDueTasks(LocalDateTime sendAt) {
        log.info("Getting all tasks for date-time {}", sendAt);
        return notificationTaskRepository.findBySendAtLessThanEqualAndSentFalse(sendAt);
    }

    public void markAsSentById(Long id) {
        log.info("Mark as sent for taskId {}", id);
        notificationTaskRepository.markAsSentById(id);
    }
}