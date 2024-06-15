package dus.gpb.khabibullinfrontendbot.service.command.impl;

import dus.gpb.khabibullinfrontendbot.service.command.Command;
import dus.gpb.khabibullinfrontendbot.service.command.CommandsEnum;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@Slf4j
public class StartCommand implements Command {

    private final MessageSender textMessageSender;

    public static final String START_COMMAND_ANSWER = "Это прототип бота в рамках Backend-академии GPB IT Factory";

    public static final String WRONG_COMMAND_ANSWER = "Команда '%s' не должна содержать ничего кроме команды и быть равной '%s'";

    private static final String LOG_PREFIX = "[Start command]";

    public StartCommand(@Autowired MessageSender textMessageSender) {
        this.textMessageSender = textMessageSender;
    }

    @Override
    public void handleCommand(Message message) {
        log.info("{} Получено сообщение, которое начинается с '{}' ",
                LOG_PREFIX,
                CommandsEnum.START.getValue());

        if (validateCommand(message.getText())) {
            log.info("{} Получена команда '{}' ",
                    LOG_PREFIX,
                    CommandsEnum.START.getValue());

            long chatId = message.getChatId();
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId, START_COMMAND_ANSWER));

            log.info("{} В чат с id {} направлено сообщение с текстом '{}'",
                    LOG_PREFIX,
                    chatId,
                    START_COMMAND_ANSWER);
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
        return CommandsEnum.START;
    }

    private boolean validateCommand(String text) {
        return text.split(" ").length == 1;
    }
}
