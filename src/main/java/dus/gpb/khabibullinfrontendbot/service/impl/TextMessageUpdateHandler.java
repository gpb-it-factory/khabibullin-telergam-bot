package dus.gpb.khabibullinfrontendbot.service.impl;

import dus.gpb.khabibullinfrontendbot.service.command.Command;
import dus.gpb.khabibullinfrontendbot.service.command.CommandsEnum;
import dus.gpb.khabibullinfrontendbot.service.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TextMessageUpdateHandler implements UpdateHandler {

    private final Map<String, Command> commands = new HashMap<>();

    public TextMessageUpdateHandler(List<Command> botCommands) {
        botCommands.forEach(command -> {
            if (this.commands.containsKey(command.getCommandEnum().getValue())) {
                log.error("Duplicate command name: {}, classes: {}, {}",
                        command.getCommandEnum(), this.commands.get(command.getCommandEnum().getValue()), command);
                throw new IllegalStateException("Duplicate command name " + command.getCommandEnum().getValue());
            }
            this.commands.put(command.getCommandEnum().getValue(), command);
        });
    }

    @Override
    public void handleUpdate(Update update) {
        if (update.getMessage() == null) {
            return;
        }

        final Message message = update.getMessage();

        if (message.hasText()) {
            handleCommand(message);
        }
    }

    @Override
    public void handleCommand(Message message) {
        String text = message.getText();
        log.info("Received message with text: {}", text);
        if (isCommand(text)) {
            commands.getOrDefault(text.split(" ")[0], commands.get(CommandsEnum.NOT_IMPLEMENTED.getValue())).handleCommand(message);
        } else {
            commands.get(CommandsEnum.NOT_IMPLEMENTED.getValue()).handleCommand(message);
        }
    }

    private boolean isCommand(String text) {
        return text.startsWith("/");
    }
}
