package pro.sky.telegrambot.provider;

import com.pengrad.telegrambot.request.SendMessage;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.format.DateTimeFormatter;

public class SendMessageProvider {

    public static SendMessage getWelcomeMessage(Long chatId) {
        return new SendMessage(chatId, "Привет! Это учебный проект бота-напоминалки\n\n" +
                "Ты можешь отправить сообщение вида:\n\n" +
                "\"01.01.2022 20:00 Сделать домашнюю работу\"\n\n" +
                "И в указанные дату и время я напомню тебе о делах!");
    }

    public static SendMessage getDefaultUnknownMessage(Long chatId) {
        return new SendMessage(chatId, "Я не понимаю. Попробуй отправить сообщение вида:\n\n" +
                "\"01.01.2022 20:00 Сделать домашнюю работу\"\n\n" +
                "И в указанные дату и время я напомню тебе о делах!");
    }

    public static SendMessage getDateInThePastMessage(Long chatId, String date) {
        return new SendMessage(chatId, "Извини, но дата \"%s\" - в прошлом, я не смогу тебе напомнить!".formatted(date));
    }

    public static SendMessage successCreatedTaskMessage(Long chatId, String date, String message) {
        return new SendMessage(chatId, "Создано напоминание \"%s\", дата напоминания: \"%s\"".formatted(message, date));
    }

    public static SendMessage notifyMessage(NotificationTask task) {
        return new SendMessage(task.getChatId(), "Напоминаю!\n\n" +
                task.getSendAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "\n\n" +
                task.getMessage());
    }
}
