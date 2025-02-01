package ru.avdeev.front_notification_service.bot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Component
@AllArgsConstructor
public class Bot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            String msg = update.getMessage().getText();

            switch (msg) {
                case "/id":
                    sendMessage(chatId, chatId.toString());
                    break;
                case "/hello":
                    sendMessage(chatId, "Привет, " + update.getMessage().getChat().getFirstName());
                    break;
                default:
                    break;
            }
        }
    }

    public void sendMessage(Long chatId, String message) {

        SendMessage msg = new SendMessage(chatId.toString(), message);

        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}