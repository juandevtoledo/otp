package com.lulobank.otp.services.utils;

import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

public class ExceptionUtils {

    public static Response getResponseBindingResult(BindingResult bindingResult) {
        String error = bindingResult
                .getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(StringUtils.EMPTY);
        return new Response(ValidatorUtils.getListValidations(error,
                String.valueOf(HttpStatus.BAD_REQUEST.value())));
    }

}
