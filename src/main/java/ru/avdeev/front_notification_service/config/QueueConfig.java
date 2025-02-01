package ru.avdeev.front_notification_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Value("${rabbit.queueFrontIn}")
    public String queueFrontIn;

    @Value("${rabbit.queueTelegramIn}")
    public String queueTelegramIn;
}
