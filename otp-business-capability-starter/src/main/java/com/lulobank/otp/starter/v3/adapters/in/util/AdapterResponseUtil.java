package com.lulobank.otp.starter.v3.adapters.in.util;

import com.lulobank.otp.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.otp.starter.v3.adapters.in.dto.GenericResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdapterResponseUtil {

    public static ResponseEntity<GenericResponse> ok() {
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity<GenericResponse> badRequest(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<GenericResponse> error(ErrorResponse errorResponse, HttpStatus httpStatus) {
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
