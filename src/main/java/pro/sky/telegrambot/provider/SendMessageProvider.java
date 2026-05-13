package pro.sky.telegrambot.provider;

import com.pengrad.telegrambot.request.SendMessage;

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
}
