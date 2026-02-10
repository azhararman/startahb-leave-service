package com.startahb.crm.leave.exception;


//import com.startahb.crm.leave.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("success", false);
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Resource not found");
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 403 - Unauthorized action
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedActionException(UnauthorizedActionException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 403);
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 409 - Name already exists (your previous handler)
//    @ExceptionHandler(NameAlreadyExist.class)
//    public ResponseEntity<ApiResponse<Void>> handleNameAlreadyExistException(NameAlreadyExist ex) {
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(ApiResponse.error(ex.getMessage(), 409));
//    }

    // 400 - Invalid input (past dates, wrong values)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 403 - Security exception (user accessing someone else's data)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 403);
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 500 - Catch-all for unhandled exceptions (always last)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("success", false);
        response.put("message", "An unexpected error occurred: " + ex.getMessage());
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
