package dus.gpb.khabibullinfrontendbot.service.command.impl;

import dus.gpb.khabibullinfrontendbot.service.command.Command;
import dus.gpb.khabibullinfrontendbot.service.command.CommandsEnum;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@Slf4j
public class PingCommand implements Command {

    private final MessageSender textMessageSender;

    public static final String PING_COMMAND_ANSWER = "pong";

    public static final String WRONG_COMMAND_ANSWER = "Команда '%s' не должна содержать ничего кроме команды и быть равной '%s'";

    private static final String LOG_PREFIX = "[Ping command]";

    public PingCommand(@Autowired MessageSender textMessageSender) {
        this.textMessageSender = textMessageSender;
    }

    @Override
    public void handleCommand(Message message) {
        log.info("{} Получено сообщение, которое начинается с '{}' ",
                LOG_PREFIX,
                CommandsEnum.PING.getValue());

        if (validateCommand(message.getText())) {

            log.info("{} Получена команда '{}' ",
                    LOG_PREFIX,
                    CommandsEnum.PING.getValue());

            long chatId = message.getChatId();
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId, PING_COMMAND_ANSWER));

            log.info("{} В чат с id {} направлено сообщение с текстом '{}'",
                    LOG_PREFIX,
                    chatId,
                    PING_COMMAND_ANSWER);
        } else {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(
                    message.getChatId(),
                    String.format(WRONG_COMMAND_ANSWER,
                            message.getText().split(" ")[0],
                            CommandsEnum.REGISTER.getValue()))
            );

            log.info("{} В чат с id {} направлено сообщение с текстом '{}'",
                    LOG_PREFIX,
                    message.getChatId(),
                    String.format(WRONG_COMMAND_ANSWER,
                            message.getText().split(" ")[0],
                            CommandsEnum.REGISTER.getValue())
            );
        }
    }

    @Override
    public CommandsEnum getCommandEnum() {
        return CommandsEnum.PING;
    }

    private boolean validateCommand(String text) {
        return text.split(" ").length == 1;
    }
}
