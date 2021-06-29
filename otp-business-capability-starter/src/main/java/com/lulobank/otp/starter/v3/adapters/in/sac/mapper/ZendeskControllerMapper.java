package com.lulobank.otp.starter.v3.adapters.in.sac.mapper;

import com.lulobank.otp.services.v3.domain.zendesk.AuthorizationSac;
import com.lulobank.otp.services.v3.domain.zendesk.VerifyAuthorizationSac;
import com.lulobank.otp.starter.v3.adapters.in.mapper.GenericResponseMapper;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.AuthorizationSacRequest;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.VerifyAuthorizationSacRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ZendeskControllerMapper extends GenericResponseMapper {
    ZendeskControllerMapper INSTANCE = Mappers.getMapper(ZendeskControllerMapper.class);

    AuthorizationSac toAuthorizationSac(AuthorizationSacRequest authorizationSacRequest);

    VerifyAuthorizationSac toVerifyAuthorizationSac(VerifyAuthorizationSacRequest verifyAuthorizationSacRequest);

}
