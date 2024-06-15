package dus.gpb.khabibullinfrontendbot.client.user;

import dus.gpb.khabibullinfrontendbot.client.BaseClient;
import dus.gpb.khabibullinfrontendbot.dto.account.CreateAccountRequest;
import dus.gpb.khabibullinfrontendbot.dto.account.CreateAccountRequestV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@Slf4j
public class AccountClient extends BaseClient {

    public static final String ACCOUNTS_API_PREFIX = "/middle/v2/users";

    private final String baseUrl;

    protected AccountClient(@Value("${service.middle.url}") String backendUrl) {
        super(RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(backendUrl + ACCOUNTS_API_PREFIX)
                .build());
        this.baseUrl = backendUrl + ACCOUNTS_API_PREFIX;
    }

    public ResponseEntity<Object> createAccount(CreateAccountRequest createAccountRequest) {

        log.info("[AccountClient] отправлен запрос get {}",  baseUrl + "/" + createAccountRequest.userId() + "/accounts");

        return post("/" + createAccountRequest.userId() + "/accounts", new CreateAccountRequestV2(createAccountRequest.accountName()));
    }
}
