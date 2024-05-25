package dus.gpb.khabibullinfrontendbot.service;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface CommandHandler {

    void handleStartCommand(Message message);

    void handlePongCommand(Message message);

    void handleTestCommand(Message message);

    void handleDefaultCommand(Message message);
}
