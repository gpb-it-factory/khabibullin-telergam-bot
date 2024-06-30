package dus.gpb.khabibullinfrontendbot.bot;

import dus.gpb.khabibullinfrontendbot.service.UpdateHandler;
import dus.gpb.khabibullinfrontendbot.service.impl.TextMessageUpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@Slf4j
public class KhabibullinFrontendTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final UpdateHandler textMessageUpdateHandler;

    private final String name;

    private final String token;


    @Autowired
    public KhabibullinFrontendTelegramBot(@Value("${bot.name}") String name,
                                          @Value("${bot.token}") String token,
                                          TextMessageUpdateHandler textMessageUpdateHandler1) {
        this.name = name;
        this.token = token;
        this.textMessageUpdateHandler = textMessageUpdateHandler1;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            textMessageUpdateHandler.handleUpdate(update);
        }
    }

    public String getName() {
        return name;
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Работает ли зарегистрированный бот: {}", botSession.isRunning());
    }
}