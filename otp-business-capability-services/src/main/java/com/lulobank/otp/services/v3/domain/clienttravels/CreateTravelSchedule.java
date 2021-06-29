package com.lulobank.otp.services.v3.domain.clienttravels;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class CreateTravelSchedule implements OTPTransactionData {

  @NotEmpty
  private String travelName;
  @Valid @NotNull
  private TravelContactInfo contactInfo;
  @Valid @NotNull
  private TravelDates travelDates;
  @Valid @NotEmpty
  private List<TravelDestination> destination;

  @Getter
  @Setter
  @EqualsAndHashCode
  public static class TravelContactInfo {
    @NotEmpty
    private String name;
    @NotEmpty
    @Pattern(regexp = "^[0-9+\\s]+$")
    private String phone;
    @NotEmpty
    @Email
    private String emailAddress;
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  public static class TravelDates {
    @NotEmpty
    private String from;
    @NotEmpty
    private String to;
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  public static class TravelDestination {
    @Valid @NotNull
    private TravelRegion region;
    @Valid @NotEmpty
    private List<TravelCountry> countries;
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  public static class TravelRegion {
    @NotEmpty
    private String code;
    @NotEmpty
    private String name;
    @NotEmpty
    private String iconId;
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  public static class TravelCountry {
    @NotEmpty
    private String code;
    @NotEmpty
    private String name;
    @NotEmpty
    private String alphaCode;
    @NotEmpty
    private String iconId;
  }

}