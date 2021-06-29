package com.lulobank.otp.starter.v3.adapters.in.mapper;

import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.starter.v3.adapters.in.dto.ErrorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenericResponseMapper {
    @Mapping(source = "businessCode", target = "code")
    @Mapping(source = "providerCode", target = "failure")
    ErrorResponse toErrorResponse(UseCaseResponseError savingsAccountResponseError);
}
