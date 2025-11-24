package apap.ti._5.accommodation_2306275600_be.external;

import java.util.Objects;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;

@Service
public class AuthServiceMockImpl implements AuthServiceMock {

    private String be1Url = "https://travel-apap-mock-server.vercel.app/";

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceMockImpl.class);

    private final RestTemplate restTemplate;

    AuthServiceMockImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String SUPERADMIN_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMSIsInJvbGUiOiJTdXBlcmFkbWluIn0.lJEnbqCnBRHd5VQGRpt2bhL6thuJc35qY5dupmg8dwI";
    private static final String ACCOMMODATION_OWNER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjFhMmIzYzRkLTVlNmYtNzA4MC05MGEwLWIxYzJkM2U0ZjUwMSIsInJvbGUiOiJBY2NvbW1vZGF0aW9uIE93bmVyIn0.UQV7EuEaokBoLv8yB3Ti-wLAujzitxZmZ6g0fF4VJPI";
    private static final String CUSTOMER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImMxYzFjMWMxLWMxYzEtYzFjMS1jMWMxLWMxYzFjMWMxYzFjMSIsInJvbGUiOiJDdXN0b21lciJ9.AIRg51HdixEkiDJP0afDZCCz1Z8EduexjZA8u85yEJs";
    
    private static final ParameterizedTypeReference<BaseResponseDTO<UserProfileDTO>> normalUserTypeRef = new ParameterizedTypeReference<BaseResponseDTO<UserProfileDTO>>() {
    };
    private static final ParameterizedTypeReference<BaseResponseDTO<CustomerProfileDTO>> customerTypeRef = new ParameterizedTypeReference<BaseResponseDTO<CustomerProfileDTO>>() {
    };

    private static final Pattern ERROR_DETAILS_PATTERN = Pattern.compile(": .*");

    @Override
    public UserProfileDTO getSuperAdminUser() {
        return fetchUserProfileWithToken(SUPERADMIN_TOKEN, "Superadmin", normalUserTypeRef);
    }

    @Override
    public UserProfileDTO getAccommodationOwnerUser() {
        return fetchUserProfileWithToken(ACCOMMODATION_OWNER_TOKEN, "Accommodation Owner", normalUserTypeRef);
    }

    @Override
    public CustomerProfileDTO getCustomerUser() {
        return fetchUserProfileWithToken(CUSTOMER_TOKEN, "Customer", customerTypeRef);
    }


    private HttpHeaders setRequestAuthHeader(String hardCodedToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + hardCodedToken);

        return headers;
    }

    private <T> T fetchAndExtractSpecificMockData(
            String url,
            String logContext,
            ParameterizedTypeReference<BaseResponseDTO<T>> responseType,
            String hardCodedToken)
            throws NullPointerException, HttpClientErrorException,
            HttpServerErrorException, ResourceAccessException {

        HttpHeaders extractedHeader = setRequestAuthHeader(hardCodedToken);

        Objects.requireNonNull(extractedHeader);
        HttpEntity<?> entity = new HttpEntity<>(extractedHeader);

        Objects.requireNonNull(url);
        HttpMethod requestMethod = HttpMethod.GET;
        Objects.requireNonNull(requestMethod);
        Objects.requireNonNull(responseType);

        ResponseEntity<BaseResponseDTO<T>> response = restTemplate.exchange(
                url,
                requestMethod,
                entity,
                responseType);

        BaseResponseDTO<T> body = response.getBody();

        if (Objects.isNull(body)) {
            logger.warn("{} response body is null", logContext);
            throw new NullPointerException(logContext + " response body is null");
        }
        Objects.requireNonNull(body);

        if (Objects.isNull(body.getData())) {
            logger.warn("{} response data is null", logContext);
            throw new NullPointerException(logContext + " response data is null");
        }

        return body.getData();

    }

    private String handleHttpException(HttpStatusCodeException e) {
        JsonNode jsonErrorBody = e.getResponseBodyAs(JsonNode.class);

        if (e instanceof HttpClientErrorException.NotFound) {
            return "" + ERROR_DETAILS_PATTERN.matcher(e.getMessage()).replaceAll("");
        }  else if (!Objects.isNull(jsonErrorBody) && jsonErrorBody.has("message")) {
            return "" + jsonErrorBody.get("message").asText();
        } else {
            return "Unhandled Exception was thrown" + "HTTP Error " + e.getStatusCode() + ": " + e.getStatusText();
        }
    }

    private <T> T fetchUserProfileWithToken(String token, String roleName,
            ParameterizedTypeReference<BaseResponseDTO<T>> typeReference) throws AccessDeniedException {
        String url = be1Url + "api/auth/user";
        T user;
        try {
            user = fetchAndExtractSpecificMockData(
                    url,
                    roleName + " User Profile",
                    typeReference,
                    token);
            return user;
        } catch (HttpStatusCodeException e) {
            String errorMsg = handleHttpException(e);
            throw new AccessDeniedException(errorMsg);
        } catch (ResourceAccessException e) {
            throw new AccessDeniedException("Connection to auth server timed out.");
        } catch (NullPointerException e) {
            throw new AccessDeniedException("NPE occurred: " + e.getMessage());
        } catch (Exception e) {
            throw new AccessDeniedException("An unexpected error occurred: " + e.getMessage());
        }
    }
}
