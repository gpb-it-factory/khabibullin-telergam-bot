package dus.gpb.khabibullinfrontendbot.service.command.impl;

import dus.gpb.khabibullinfrontendbot.client.user.AccountClient;
import dus.gpb.khabibullinfrontendbot.dto.account.CreateAccountRequest;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import dus.gpb.khabibullinfrontendbot.service.command.Command;
import dus.gpb.khabibullinfrontendbot.service.command.CommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@Slf4j
public class CreateAccountCommand implements Command {

    private final MessageSender textMessageSender;

    private final AccountClient accountClient;

    public static final String CREATE_ACCOUNT_COMMAND_ON_SUCCESS_ANSWER = "Пользователю с именем '%s' успешно создан счёт с именем '%s'";

    public static final String CREATE_ACCOUNT_COMMAND_ON_NOT_REGISTERED_ANSWER = "Пользователь с именем '%s' не зарегистрирован";

    public static final String CREATE_ACCOUNT_COMMAND_ON_CONFLICT_ANSWER = "У пользователя '%s' уже есть аккаунт";

    public static final String ERROR_ANSWER = "Что-то пошло не так";

    public static final String WRONG_COMMAND_ANSWER = "Команда '%s' не должна содержать ничего кроме команды и быть равной '%s'";

    private static final String LOG_PREFIX = "[Create account command]";

    public CreateAccountCommand(@Autowired MessageSender textMessageSender,
                                AccountClient accountClient) {
        this.textMessageSender = textMessageSender;
        this.accountClient = accountClient;
    }

    @Override
    public void handleCommand(Message message) {
        log.info("{} Получено сообщение, которое начинается с '{}' ",
                LOG_PREFIX,
                CommandsEnum.CREATE_ACCOUNT.getValue());

        if (validateCommand(message.getText())) {
            log.info("{} Получена команда '{}' ",
                    LOG_PREFIX,
                    CommandsEnum.CREATE_ACCOUNT.getValue());

            long chatId = message.getChatId();


            CreateAccountRequest accountToCreate = new CreateAccountRequest(
                    message.getFrom().getUserName() != null ? message.getFrom().getUserName() + "'s Account" : "The First and The Only",
                    message.getFrom().getId(),
                    message.getFrom().getUserName()
            );

            executeSendMessageDependsOnResponse(
                    chatId,
                    accountToCreate,
                    accountClient.createAccount(accountToCreate)
            );

        } else {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(
                    message.getChatId(),
                    String.format(WRONG_COMMAND_ANSWER,
                            message.getText().split(" ")[0],
                            CommandsEnum.CREATE_ACCOUNT.getValue()))
            );


            log.info(getLogMessage(message.getChatId(),
                    String.format(WRONG_COMMAND_ANSWER,
                            message.getText().split(" ")[0],
                            CommandsEnum.CREATE_ACCOUNT.getValue()))
            );
        }
    }

    @Override
    public CommandsEnum getCommandEnum() {
        return CommandsEnum.CREATE_ACCOUNT;
    }

    private boolean validateCommand(String text) {
        return text.split(" ").length == 1 && text.split(" ")[0].equals(CommandsEnum.CREATE_ACCOUNT.getValue());
    }

    private void executeSendMessageDependsOnResponse(
            long chatId,
            CreateAccountRequest accountToCreate,
            ResponseEntity<Object> middleResponse
    ) {
        if (middleResponse.getStatusCode().is2xxSuccessful()) {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(
                    chatId,
                    String.format(CREATE_ACCOUNT_COMMAND_ON_SUCCESS_ANSWER,
                            accountToCreate.userName(),
                            accountToCreate.accountName()))
            );

            log.info(getLogMessage(
                    chatId,
                    String.format(CREATE_ACCOUNT_COMMAND_ON_SUCCESS_ANSWER,
                            accountToCreate.userName(),
                            accountToCreate.accountName()))
            );

            return;
        }

        if (middleResponse.getStatusCode().equals(HttpStatusCode.valueOf(401))) {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId,
                    String.format(CREATE_ACCOUNT_COMMAND_ON_NOT_REGISTERED_ANSWER, accountToCreate.userName())));

            log.info(getLogMessage(chatId,
                    String.format(CREATE_ACCOUNT_COMMAND_ON_NOT_REGISTERED_ANSWER, accountToCreate.userName())
            ));

            return;
        }

        if (middleResponse.getStatusCode().equals(HttpStatusCode.valueOf(409))) {
            textMessageSender.executeSendMessage(textMessageSender.getSendMessage(chatId,
                    String.format(CREATE_ACCOUNT_COMMAND_ON_CONFLICT_ANSWER, accountToCreate.userName())));

            log.info(getLogMessage(chatId,
                    String.format(CREATE_ACCOUNT_COMMAND_ON_CONFLICT_ANSWER, accountToCreate.userName())
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
