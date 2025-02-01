package ru.avdeev.front_notification_service.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class BotProperties {

    private String token;
    private List<Long> receivers;
}