package com.lulobank.otp.services.inboundadapters.mapper;

import com.lulobank.otp.sdk.dto.credits.GenerateOtpForNewLoan;
import com.lulobank.otp.services.features.otp.GenerateOtpForNewLoanCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenerateOtpForNewLoanMapper {

    GenerateOtpForNewLoanMapper INSTANCE = Mappers.getMapper(GenerateOtpForNewLoanMapper.class);

    @Mappings({
            @Mapping(source = "idClient", target = "idClient"),
            @Mapping(source = "generateOtpForNewLoan.idCredit", target = "idCredit"),
            @Mapping(source = "generateOtpForNewLoan.idOffer", target = "idOffer")
    })
    GenerateOtpForNewLoanCommand generateOtpForNewLoanToGenerateOtpForNewLoanCommand(GenerateOtpForNewLoan generateOtpForNewLoan, String idClient);

}
