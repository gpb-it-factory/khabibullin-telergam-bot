package dus.gpb.khabibullinfrontendbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface MessageSender {

    SendMessage getSendMessage(long chatId, String answerMessage);

    void executeSendMessage(SendMessage messageToExecute);
}
