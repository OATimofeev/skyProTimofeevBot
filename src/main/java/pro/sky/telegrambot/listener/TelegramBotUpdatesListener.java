package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.constant.Constant;
import pro.sky.telegrambot.provider.SendMessageProvider;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            log.info("Processing update: {}", update);
            try {
                if (update.message() == null || update.message().text() == null) {
                    return;
                }
                if (update.message().text().equals("/start")) {
                    telegramBot
                            .execute(SendMessageProvider.getWelcomeMessage(update.message().chat().id()));
                } else if (Constant.MESSAGE_PATTERN.matcher(update.message().text()).matches()) {
                    telegramBot
                            .execute(notificationTaskService.createTask(update.message().chat().id(), update.message().text()));
                } else {
                    telegramBot
                            .execute(SendMessageProvider.getDefaultUnknownMessage(update.message().chat().id()));
                }
            } catch (Exception e) {
                log.error("Error with processing update: chatId {}. Stacktrace: ", update.message().chat().id(), e);
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
