package dus.gpb.khabibullinfrontendbot.service.command;

public enum CommandsEnum {

    START("/start"),
    PING("/ping"),
    REGISTER("/register"),
    CREATE_ACCOUNT("/create_account"),
    CURRENT_BALANCE("/current_balance"),
    TRANSFER("/transfer"),
    WRONG_COMMAND("/wrong_command_format"),
    NOT_IMPLEMENTED("/not_implemented_command");

    private final String value;

    CommandsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
