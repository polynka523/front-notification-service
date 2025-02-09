package ru.avdeev.front_notification_service.bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.avdeev.front_notification_service.dto.Booking;
import ru.avdeev.front_notification_service.dto.Order;
import ru.avdeev.front_notification_service.rabbit.OrderSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Component
@AllArgsConstructor
@Slf4j
public class Bot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    private final OrderSender orderSender;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            String msg = update.getMessage().getText();

            switch (msg) {
                case "/id":
                    sendMessage(chatId, chatId.toString(),null);
                    break;
                case "/hello":
                    sendMessage(chatId, "Привет, " + update.getMessage().getChat().getFirstName(),null);
                    break;
                default:
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            String[] words = data.split(" ");
            String callBack = words[0];
            String id = words[1];
            Order order = new Order();
            order.setId(UUID.fromString(id));
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
            editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            if(callBack.equals("decline")){
                log.info("Declined user id: " + id);
                orderSender.sendMessage(order,"OrderCancel");
                editMessageReplyMarkup.setReplyMarkup(null);
                try {
                    telegramClient.execute(editMessageReplyMarkup);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (callBack.equals("accept")) {
                log.info("Accepted user id: " + id);
                orderSender.sendMessage(order,"OrderPayed");
                editMessageReplyMarkup.setReplyMarkup(null);
                try {
                    telegramClient.execute(editMessageReplyMarkup);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    public void sendMessage(Long chatId, String message,String uid) {

        SendMessage msg = new SendMessage(chatId.toString(), message);
        msg.setReplyMarkup(setInline(uid));
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    private InlineKeyboardMarkup setInline(String uid) {
        InlineKeyboardButton declineButton = new InlineKeyboardButton("Отклонить");
        declineButton.setCallbackData("decline "+uid);
        InlineKeyboardButton acceptButton = new InlineKeyboardButton("Принять");
        acceptButton.setCallbackData("accept "+uid);
        InlineKeyboardRow row = new InlineKeyboardRow(acceptButton,declineButton);
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        return  markup;

    }
//    private void keyboardDisplayNone(Update update, SendMessage msg){
//        if(update.hasCallbackQuery()){
//            msg.setReplyMarkup(new ReplyKeyboardRemove(true));
//        }
//    }
}