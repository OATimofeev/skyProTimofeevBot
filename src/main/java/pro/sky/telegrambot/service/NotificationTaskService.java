package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.constant.Constant;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.provider.SendMessageProvider;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NotificationTaskService {

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    public SendMessage createTask(Long chatId, String text) {
        log.info("Creating new task for chatId = '{}' with text = '{}'", chatId, text);
        Matcher matcher = Pattern.compile(Constant.MESSAGE_REGEX).matcher(text);
        if (!matcher.matches()) {
            log.error("Fail parse message '{}' from chatId = '{}'! We're not suppose to be here!!!", text, chatId);
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
                        .message(matcher.group(3))
                        .build());

        log.info("Created new task for chatId = '{}' with text = '{}'", chatId, text);
        return SendMessageProvider.successCreatedTaskMessage(chatId, matcher.group(1), matcher.group(3));
    }
}