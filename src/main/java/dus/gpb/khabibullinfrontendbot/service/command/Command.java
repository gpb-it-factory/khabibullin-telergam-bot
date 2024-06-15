package dus.gpb.khabibullinfrontendbot.service.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface Command {

    void handleCommand(Message message);

    String getCommandValue();

}
