package dus.gpb.khabibullinfrontendbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface UpdateHandler {

    void handleUpdate(Update update);

    void handleCommand(Message message);
}
