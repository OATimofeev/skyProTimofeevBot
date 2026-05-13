package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.constant.Constant;
import pro.sky.telegrambot.model.NotificationTask;
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
        log.info("Creating new task for chatId = {} with text = {}", chatId, text);
        Matcher matcher = Pattern.compile(Constant.MESSAGE_REGEX).matcher(text);

        notificationTaskRepository
                .save(NotificationTask
                        .builder()
                        .chatId(chatId)
                        .sendAt(LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                        .message(matcher.group(2))
                        .build());

        return new SendMessage(chatId, "Создано напоминание \"%s\", дата напоминания: \"%s\"".formatted(matcher.group(2), matcher.group(1)));
    }
}