package dus.gpb.khabibullinfrontendbot.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class BaseClient {

    protected final RestTemplate rest;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> middleServiceResponse;
        try {
            middleServiceResponse = rest.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return manageMiddleServiceResponse(middleServiceResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private ResponseEntity<Object> manageMiddleServiceResponse(ResponseEntity<Object> middleServiceResponse) {
        if (middleServiceResponse.getStatusCode().is2xxSuccessful()) {
            return middleServiceResponse;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(middleServiceResponse.getStatusCode());

        if (middleServiceResponse.hasBody()) {
            return responseBuilder.body(middleServiceResponse.getBody());
        }

        return responseBuilder.build();
    }
}
