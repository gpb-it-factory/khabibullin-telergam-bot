package dus.gpb.khabibullinfrontendbot.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.List;

public abstract class BaseClient {

    protected final RestClient rest;

    protected BaseClient(RestClient rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null);
    }

    protected <T> List<T> getList(String path) {
        return makeAndSendRequestForList(path);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable T body) {

        ResponseEntity<Object> backendServiceResponse;

        try {
            if (body == null) {
                backendServiceResponse = rest.method(method)
                        .uri(path)
                        .retrieve()
                        .toEntity(Object.class);
            } else {
                backendServiceResponse = rest.method(method)
                        .uri(path)
                        .body(body)
                        .retrieve()
                        .toEntity(Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return manageMiddleServiceResponse(backendServiceResponse);
    }

    private <T> List<T> makeAndSendRequestForList(String path) {
        return rest.get().uri(path).retrieve().toEntity(new ParameterizedTypeReference<List<T>>() {
        }).getBody();
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
