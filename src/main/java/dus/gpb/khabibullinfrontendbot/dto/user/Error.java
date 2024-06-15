package dus.gpb.khabibullinfrontendbot.dto.user;

import java.util.UUID;

public record Error(String message, String type, String code, UUID traceId) {

}

