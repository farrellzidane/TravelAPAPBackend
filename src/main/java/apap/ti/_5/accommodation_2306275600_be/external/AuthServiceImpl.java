package apap.ti._5.accommodation_2306275600_be.external;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserRoleDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final RestTemplate restTemplate;

    AuthServiceImpl(
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String SUPERADMIN = "Superadmin";
    private static final String ACCOMMODATION_OWNER = "Accommodation Owner";
    private static final String CUSTOMER = "Customer";

    // @Value("${external.sidating-app-be-url}")
    private String be1Url = "https://travel-apap-mock-server.vercel.app";

    private static final Pattern ERROR_DETAILS_PATTERN = Pattern.compile(": .*");

    private HttpHeaders getRequestAuthHeader() {
        HttpHeaders headers = new HttpHeaders();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!Objects.isNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (!Objects.isNull(authHeader) && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }
        }

        return headers;
    }

    private <T> T fetchAndExtractSpecificData(
            String url,
            String logContext,
            ParameterizedTypeReference<BaseResponseDTO<T>> responseType)
            throws NullPointerException, HttpClientErrorException,
            HttpServerErrorException, ResourceAccessException {

        HttpHeaders extractedHeader = getRequestAuthHeader();
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

    private <T> T postAndExtractSpecificData(
            String url,
            Object requestBody,
            String logContext,
            ParameterizedTypeReference<BaseResponseDTO<T>> responseType)
            throws NullPointerException, HttpClientErrorException,
            HttpServerErrorException, ResourceAccessException {
        HttpHeaders extractedHeader = getRequestAuthHeader();
        Objects.requireNonNull(extractedHeader);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, extractedHeader);

        Objects.requireNonNull(url);

        HttpMethod requestMethod = HttpMethod.POST;

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

    @Override
    public UserProfileDTO getAuthenticatedUser() throws AccessDeniedException {
        String url = be1Url + "/api/auth/user";

        UserProfileDTO user;
        try {
            user = fetchAndExtractSpecificData(
                    url,
                    "User Profile",
                    new ParameterizedTypeReference<BaseResponseDTO<UserProfileDTO>>() {
                    });
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

    private UserRoleDTO getUserRole(UUID userId) {
        String url = be1Url + "/api/auth/role";

        UserRoleDTO userRole;
        try {
            userRole = postAndExtractSpecificData(
                    url,
                    userId,
                    "User Role",
                    new ParameterizedTypeReference<BaseResponseDTO<UserRoleDTO>>() {
                    });
            return userRole;
        } catch (HttpStatusCodeException e) {
            String errMsg = handleHttpException(e);

            throw new AccessDeniedException(errMsg);
        } catch (ResourceAccessException e) {
            throw new AccessDeniedException("Connection to auth server failed or timed out.");

        } catch (Exception e) {
            throw new AccessDeniedException("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public boolean isSuperAdmin(UserProfileDTO UserProfileDTO) {
        return UserProfileDTO.role().equalsIgnoreCase(SUPERADMIN);
    }

    @Override
    public boolean isAccommodationOwner(UserProfileDTO UserProfileDTO) {
        return UserProfileDTO.role().equalsIgnoreCase(ACCOMMODATION_OWNER);
    }

    @Override
    public boolean isCustomer(UserProfileDTO UserProfileDTO) {
        return UserProfileDTO.role().equalsIgnoreCase(CUSTOMER);
    }


    @Override
    public boolean isSuperAdmin(UUID userId) {
        UserRoleDTO userRole = getUserRole(userId);

        return userRole.role().equalsIgnoreCase("Superadmin");
    }

    @Override
    public boolean isAccommodationOwner(UUID userId) {
        UserRoleDTO userRole = getUserRole(userId);

        return userRole.role().equalsIgnoreCase("Accommodation Owner");
    }

    @Override
    public boolean isCustomer(UUID userId) {
        UserRoleDTO userRole = getUserRole(userId);

        return userRole.role().equalsIgnoreCase("Customer");
    }

    @Override
    public CustomerProfileDTO getCustomerProfile(UUID userId) throws NoSuchElementException {
        String url = be1Url + "/api/auth/detail?id=" + userId.toString();

        CustomerProfileDTO customerProfile;
        try {
            customerProfile = postAndExtractSpecificData(
                    url,
                    userId,
                    "Customer",
                    new ParameterizedTypeReference<BaseResponseDTO<CustomerProfileDTO>>() {
                    });
            return customerProfile;
        } catch (HttpStatusCodeException e) {
            String errMsg = handleHttpException(e);
            throw new AccessDeniedException(errMsg);
        } catch (ResourceAccessException e) {
            throw new AccessDeniedException("Connection to auth server failed or timed out.");
        } catch (NullPointerException e) {
            throw new NoSuchElementException("User Tidak ditemukan");
        } catch (Exception e) {
            throw new AccessDeniedException("An unexpected error occurred: " + e.getMessage());
        }
    }

    public boolean isCustomer(CustomerProfileDTO customerProfile) {
        return customerProfile.role().equalsIgnoreCase(CUSTOMER);
    }
}
