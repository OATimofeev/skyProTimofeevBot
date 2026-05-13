package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.constant.Constant;
import pro.sky.telegrambot.provider.SendMessageProvider;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.List;

@Service
@Slf4j
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskService service;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            log.info("Processing update: {}", update);
            if (update.message().text().equals("/start")) {
                telegramBot
                        .execute(SendMessageProvider.getWelcomeMessage(update.message().chat().id()));
            } else if (update.message().text().matches(Constant.MESSAGE_REGEX)) {
                telegramBot
                        .execute(service.createTask(update.message().chat().id(), update.message().text()));
            } else {
                telegramBot
                        .execute(SendMessageProvider.getDefaultUnknownMessage(update.message().chat().id()));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
