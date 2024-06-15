package dus.gpb.khabibullinfrontendbot.service.impl;

import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class TextMessageSender implements MessageSender {

    private final TelegramClient telegramClient;

    public TextMessageSender(@Value("${bot.token}") String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public SendMessage getSendMessage(long chatId, String answerMessage) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(answerMessage)
                .build();
    }


    @Override
    public void executeSendMessage(SendMessage messageToExecute) {
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
