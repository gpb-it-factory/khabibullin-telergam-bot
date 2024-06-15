package dus.gpb.khabibullinfrontendbot.client.user;

import dus.gpb.khabibullinfrontendbot.client.BaseClient;
import dus.gpb.khabibullinfrontendbot.dto.user.CreateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class UserClient extends BaseClient {

    public static final String API_PREFIX = "/middle/v2/users";

    protected UserClient(@Value("${service.middle.url}") String middleServiceUrl,
                         RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(middleServiceUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build());
    }


    public ResponseEntity<Object> register(CreateUserRequest createUserRequest) {
        return post("", createUserRequest);
    }
}
