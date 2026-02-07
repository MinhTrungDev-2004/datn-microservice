package datn.category_service.controllers;

import datn.category_service.exceptions.UserMessageException;
import datn.category_service.models.global.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.function.Supplier;

@Controller
public abstract class ApiBaseController {
    protected <T> ResponseEntity<ApiResult<T>> exeResponseEntity(Supplier<ApiResult<T>> supplier) {
        try {
            ApiResult<T> result = supplier.get();
            return ResponseEntity.ok(result);
        } catch (UserMessageException ex) {
            return ResponseEntity.badRequest().body(ApiResult.fail(ex.getMessage()));
        } catch (Exception ex) {
            ApiResult<T> error = new ApiResult<>();
            error.setStatus(false);
            error.setUserMessage("Đã có lỗi xảy ra");
            error.setInternalMessage(ex.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
