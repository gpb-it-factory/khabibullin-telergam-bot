package dus.gpb.khabibullinfrontendbot.client.user;

import dus.gpb.khabibullinfrontendbot.client.BaseClient;
import dus.gpb.khabibullinfrontendbot.dto.account.CreateAccountRequest;
import dus.gpb.khabibullinfrontendbot.dto.account.CreateAccountRequestV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class AccountClient extends BaseClient {

    public static final String ACCOUNTS_API_PREFIX = "/middle/v2/users";

    protected AccountClient(@Value("${service.middle.url}") String middleServiceUrl,
                            RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(middleServiceUrl + ACCOUNTS_API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build());
    }

    public ResponseEntity<Object> createAccount(CreateAccountRequest createAccountRequest) {
        return post("/" + createAccountRequest.userId() + "/accounts", new CreateAccountRequestV2(createAccountRequest.accountName()));
    }
}
