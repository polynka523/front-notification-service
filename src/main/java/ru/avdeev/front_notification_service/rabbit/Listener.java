package ru.avdeev.front_notification_service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.avdeev.front_notification_service.bot.Bot;
import ru.avdeev.front_notification_service.bot.BotProperties;
import ru.avdeev.front_notification_service.config.QueueConfig;
import ru.avdeev.front_notification_service.dto.Booking;
import ru.avdeev.front_notification_service.repository.EventRepository;

import java.util.Objects;

@Component
@EnableRabbit
@AllArgsConstructor
public class Listener {
    private final BotProperties botProperties;
    private final Logger log = LoggerFactory.getLogger(Listener.class);
    private final EventRepository eventRepository;
    private final QueueConfig queueConfig;
    private final Bot bot;

    //@RabbitListener(queues = "front.in")
    @RabbitListener(queues = "#{queueConfig.queueFrontIn}")
    public void onMessage(Message<Booking> message) {

        String type = Objects.requireNonNull(message.getHeaders().get("type")).toString();
        Booking booking = message.getPayload();
        log.info("Received message\ntype: {}\nbody: {}", type, booking);
        eventRepository.put(booking)
                .subscribe();
    }

    @RabbitListener(queues = "#{queueConfig.queueTelegramIn}")
    public void onMessage1(Booking msg) throws JsonProcessingException {
        log.info("Receive message: {}", msg);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Забронирован стол 2. Дата: ")
                .append(msg.bookingDate())
                .append(". Время начала: ")
                .append(msg.startTime())
                .append(". Продолжительность: ")
                .append(msg.count())
                .append("ч.");
        botProperties.getReceivers().stream().forEach(chatId -> bot.sendMessage(chatId, stringBuilder.toString()));
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //BookingCreated bookingCreated = mapper.readValue(new String(msg.getBody()), BookingCreated.class);
        //bookingCreated.printTest();
    }
}
