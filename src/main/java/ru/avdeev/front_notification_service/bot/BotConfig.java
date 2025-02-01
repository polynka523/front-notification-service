package ru.avdeev.front_notification_service.bot;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@AllArgsConstructor
public class BotConfig {

    private final BotProperties botProperties;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botProperties.getToken());
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication() {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public BotSession botSession(TelegramBotsLongPollingApplication application, Bot bot) {
        try {
            return application.registerBot(botProperties.getToken(), bot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}