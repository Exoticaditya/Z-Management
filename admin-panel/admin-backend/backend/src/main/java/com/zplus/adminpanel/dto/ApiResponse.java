package com.zplus.adminpanel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    // Manual constructor
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a successful response with the given message and data
     */
    public static <T> ApiResponse<T> success(String message, @Nullable T data) {
        return new ApiResponse<T>(true, message, data);
    }

    /**
     * Creates a successful response with the given data and default message
     */
    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<T>(true, "Operation successful", data);
    }

    /**
     * Creates an error response with the given message and no data
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Creates an error response with the given message and data
     */
    public static <T> ApiResponse<T> error(String message, @Nullable T data) {
        return new ApiResponse<>(false, message, data);
    }
}
