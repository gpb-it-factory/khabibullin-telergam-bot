package dus.gpb.khabibullinfrontendbot.service.command.impl;

import dus.gpb.khabibullinfrontendbot.client.user.UserClient;
import dus.gpb.khabibullinfrontendbot.dto.user.CreateUserRequest;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import dus.gpb.khabibullinfrontendbot.service.command.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@Slf4j
public class RegisterCommand implements Command {

    private final MessageSender textMessageSender;

    private final UserClient userClient;

    public static final String REGISTER_COMMAND_ON_SUCCESS_ANSWER = "Пользователь с именем '%s' успешно зарегистрирован";

    public static final String REGISTER_COMMAND_ON_CONFLICT_ANSWER = "Пользователь с именем '%s' уже зарегистрирован";

    public static final String ERROR_ANSWER = "Что-то пошло не так";

    public static final String WRONG_COMMAND_ANSWER = "Команда '%s' не должна содержать ничего кроме команды и быть равной '%s'";

    private static final String LOG_PREFIX = "[Register command]";

    public static final String REGISTER_COMMAND_VALUE = "/register";

    @Autowired
    public RegisterCommand(MessageSender textMessageSender,
                           UserClient userClient1) {
        this.textMessageSender = textMessageSender;
        this.userClient = userClient1;
    }

    @Override
    public void handleCommand(Message message) {
        log.info("{} Получено сообщение, которое начинается с '{}' ",
                LOG_PREFIX,
                REGISTER_COMMAND_VALUE);

        if (validateCommand(message.getText())) {
            log.info("{} Получена команда '{}' ",
                    LOG_PREFIX,
                    REGISTER_COMMAND_VALUE);

            long chatId = message.getChatId();
            User userToRegister = message.getFrom();
            executeSendMessageDependsOnResponse(chatId, userToRegister, userClient.register(
                            new CreateUserRequest(
                                    userToRegister.getId(),
                                    userToRegister.getUserName())
                    )
            );
        } else {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(
                    message.getChatId(),
                    String.format(WRONG_COMMAND_ANSWER,
                            message.getText().split(" ")[0],
                            REGISTER_COMMAND_VALUE))
            );


            log.info(getLogMessage(message.getChatId(),
                    String.format(WRONG_COMMAND_ANSWER,
                            message.getText().split(" ")[0],
                            REGISTER_COMMAND_VALUE))
            );
        }
    }

    @Override
    public String getCommandValue() {
        return REGISTER_COMMAND_VALUE;
    }

    private boolean validateCommand(String text) {
        return text.split(" ").length == 1 && text.split(" ")[0].equals(REGISTER_COMMAND_VALUE);
    }

    private void executeSendMessageDependsOnResponse(long chatId, User userToRegister, ResponseEntity<Object> middleResponse) {
        if (middleResponse.getStatusCode().is2xxSuccessful()) {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId,
                    String.format(REGISTER_COMMAND_ON_SUCCESS_ANSWER, userToRegister.getUserName())));

            log.info(getLogMessage(chatId,
                    String.format(REGISTER_COMMAND_ON_CONFLICT_ANSWER, userToRegister.getUserName())

            ));

            return;
        }

        if (middleResponse.getStatusCode().equals(HttpStatusCode.valueOf(409))) {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId,
                    String.format(REGISTER_COMMAND_ON_CONFLICT_ANSWER, userToRegister.getUserName())));

            log.info(getLogMessage(chatId,
                    String.format(REGISTER_COMMAND_ON_CONFLICT_ANSWER, userToRegister.getUserName())
            ));

            return;
        }

        if (middleResponse.getStatusCode().is4xxClientError() || middleResponse.getStatusCode().is5xxServerError()) {

            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId,
                    ERROR_ANSWER));

            log.info(getLogMessage(chatId, ERROR_ANSWER));
        }
    }

    private String getLogMessage(long chatId, String message) {

        return String.format(
                "%s В чат с id %s направлено сообщение с текстом '%s'",
                LOG_PREFIX,
                chatId,
                message
        );
    }
}
