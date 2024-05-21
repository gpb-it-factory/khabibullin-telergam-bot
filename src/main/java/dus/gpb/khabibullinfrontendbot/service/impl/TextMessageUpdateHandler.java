package dus.gpb.khabibullinfrontendbot.service.impl;

import dus.gpb.khabibullinfrontendbot.service.CommandHandler;
import dus.gpb.khabibullinfrontendbot.service.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
public class TextMessageUpdateHandler implements UpdateHandler {

    private final String START_COMMAND = "/start";

    private final String PONG_COMMAND = "/ping";

    private final String TEST_COMMAND = "/test";

    private final CommandHandler textCommandHandler;

    @Autowired
    public TextMessageUpdateHandler(CommandHandler textCommandHandler) {
        this.textCommandHandler = textCommandHandler;
    }

    @Override
    public void handleUpdate(Update update) {
        if (update.getMessage() == null) {
            return;
        }
        final Message message = update.getMessage();
        handleCommand(message);
    }

    @Override
    public void handleCommand(Message message) {
        final String text = message.getText();
        switch (text) {
            case START_COMMAND -> textCommandHandler.handleStartCommand(message);
            case PONG_COMMAND -> textCommandHandler.handlePongCommand(message);
            case TEST_COMMAND -> textCommandHandler.handleTestCommand(message);
            default -> textCommandHandler.handleDefaultCommand(message);
        }
    }
}
