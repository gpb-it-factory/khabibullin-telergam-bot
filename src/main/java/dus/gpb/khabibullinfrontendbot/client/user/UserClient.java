package dus.gpb.khabibullinfrontendbot.client.user;

import dus.gpb.khabibullinfrontendbot.client.BaseClient;
import dus.gpb.khabibullinfrontendbot.dto.user.CreateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class UserClient extends BaseClient {

    public static final String USERS_API_PREFIX = "/middle/v2/users";

    private final String baseUrl;

    protected UserClient(@Value("${service.middle.url}") String middleUrl) {
        super(RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(middleUrl + USERS_API_PREFIX)
                .build());
        this.baseUrl = middleUrl + USERS_API_PREFIX;
    }


    public ResponseEntity<Object> register(CreateUserRequest createUserRequest) {

        log.info("[UserClient] отправлен запрос post {} c телом {}", baseUrl, createUserRequest);

        return post("", createUserRequest);
    }
}
