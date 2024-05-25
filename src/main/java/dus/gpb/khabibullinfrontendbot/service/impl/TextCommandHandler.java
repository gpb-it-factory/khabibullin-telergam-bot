package dus.gpb.khabibullinfrontendbot.service.impl;

import dus.gpb.khabibullinfrontendbot.service.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Slf4j
public class TextCommandHandler implements CommandHandler {

    private final TelegramClient telegramClient;

    private final String START_COMMAND_ANSWER = "Это прототип бота в рамках Backend-академии GPB IT Factory";

    private final String PONG_COMMAND_ANSWER = "pong";

    private final String TEST_COMMAND_ANSWER = "Тестовое сообщения для оценки сложности расширения";

    private final String DEFAULT_ANSWER = "Команда '%s' отсутствует";

    public TextCommandHandler(@Value("${bot.token}") String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void handleStartCommand(Message message) {
        long chatId = message.getChatId();
        executeSendMessage(getSendMessage(chatId, START_COMMAND_ANSWER));
    }


    @Override
    public void handlePongCommand(Message message) {
        long chatId = message.getChatId();
        executeSendMessage(getSendMessage(chatId, PONG_COMMAND_ANSWER));
    }

    @Override
    public void handleTestCommand(Message message) {
        long chatId = message.getChatId();
        executeSendMessage(getSendMessage(chatId, TEST_COMMAND_ANSWER));
    }

    @Override
    public void handleDefaultCommand(Message message) {
        long chatId = message.getChatId();
        executeSendMessage(getSendMessage(chatId, String.format(DEFAULT_ANSWER, message.getText())));
    }

    private SendMessage getSendMessage(long chatId, String answerMessage) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(answerMessage)
                .build();
    }

    private void executeSendMessage(SendMessage messageToExecute) {
        try {
            telegramClient.execute(messageToExecute);
            log.info("Сообщение пользователю {}  отправлено", messageToExecute.getChatId());
        } catch (
                TelegramApiException e) {
            log.error("Сообщение пользователю {} c текстом {} не отправлено",
                    messageToExecute.getChatId(),
                    messageToExecute.getText());
        }
    }
}
