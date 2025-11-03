package apap.ti._5.accommodation_2306275600_be.restdto.response;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private String timestamp;
    private T data;

    public BaseResponseDTO() {
        this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public BaseResponseDTO(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // timestamp is normally set automatically, but allow overriding if needed
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> BaseResponseDTO<T> success(T data) {
        return new BaseResponseDTO<>(200, "Success", data);
    }

    public static <T> BaseResponseDTO<T> of(int status, String message, T data) {
        return new BaseResponseDTO<>(status, message, data);
    }

    @Override
    public String toString() {
        return "BaseResponseDTO{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                '}';
    }
}
