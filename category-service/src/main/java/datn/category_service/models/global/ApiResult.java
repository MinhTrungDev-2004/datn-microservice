package datn.category_service.models.global;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApiResult<T> {
    private boolean status = false;
    private String userMessage;
    private String traceID = UUID.randomUUID().toString();
    private String internalMessage;
    private T data;

    public static <T> ApiResult<T> success(T data, String userMessage) {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(true);
        result.setData(data);
        result.setUserMessage(userMessage);
        return result;
    }

    public static <T> ApiResult<T> fail(String userMessage) {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(false);
        result.setUserMessage(userMessage);
        return result;
    }
}
