package com.lulobank.otp.starter.v3.adapters.out.clients.mapper;

import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.starter.v3.adapters.out.clients.dto.ClientInformationByIdCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientsServiceMapper {
    ClientsServiceMapper INSTANCE = Mappers.getMapper(ClientsServiceMapper.class);

    @Mapping(source = "emailAddress", target = "email")
    @Mapping(source = "phoneNumber", target = "phone")
    @Mapping(source = "phonePrefix", target = "prefix")
    ClientNotifyInfo toDomain(ClientInformationByIdCard clientInformationByIdCard);

}
