package dus.gpb.khabibullinfrontendbot.service.impl;

import dus.gpb.khabibullinfrontendbot.service.command.Command;
import dus.gpb.khabibullinfrontendbot.service.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dus.gpb.khabibullinfrontendbot.service.command.impl.DefaultCommand.DEFAULT_COMMAND_VALUE;

@Service
@Slf4j
public class TextMessageUpdateHandler implements UpdateHandler {

    private final Map<String, Command> commands = new HashMap<>();

    public TextMessageUpdateHandler(List<Command> botCommands) {
        botCommands.forEach(command -> {
            if (this.commands.containsKey(command.getCommandValue())) {
                log.error("Duplicate command name: {}, classes: {}, {}",
                        command.getCommandValue(), this.commands.get(command.getCommandValue()), command);
                throw new IllegalStateException("Duplicate command name");
            }
            this.commands.put(command.getCommandValue(), command);
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
            commands.getOrDefault(text.split(" ")[0], commands.get(DEFAULT_COMMAND_VALUE)).handleCommand(message);
        } else {
            commands.get(DEFAULT_COMMAND_VALUE).handleCommand(message);
        }
    }

    private boolean isCommand(String text) {
        return text.startsWith("/");
    }
}
