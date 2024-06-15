package dus.gpb.khabibullinfrontendbot.service.command.impl;

import dus.gpb.khabibullinfrontendbot.service.command.Command;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@Slf4j
public class DefaultCommand implements Command {

    private final MessageSender textMessageSender;

    public static final String DEFAULT_ANSWER = "Команда '%s' отсутствует";

    private static final String LOG_PREFIX = "[Default command]";

    public static final String DEFAULT_COMMAND_VALUE = "/notimplementedcommand";

    public DefaultCommand(MessageSender textMessageSender) {
        this.textMessageSender = textMessageSender;
    }

    @Override
    public void handleCommand(Message message) {
        long chatId = message.getChatId();
        textMessageSender.executeSendMessage(textMessageSender.getSendMessage(
                chatId,
                String.format(DEFAULT_ANSWER, message.getText()))
        );

        log.info("{} В чат с id {} направлено сообщение с текстом '{}'",
                LOG_PREFIX,
                chatId,
                String.format(DEFAULT_ANSWER, message.getText()));
    }

    @Override
    public String getCommandValue() {
        return DEFAULT_COMMAND_VALUE;
    }
}
