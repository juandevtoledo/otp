package com.lulobank.otp.services.v3.domain.clienttravels;

import com.lulobank.otp.services.v3.domain.clienttravels.CreateTravelSchedule.TravelContactInfo;
import com.lulobank.otp.services.v3.domain.clienttravels.CreateTravelSchedule.TravelCountry;
import com.lulobank.otp.services.v3.domain.clienttravels.CreateTravelSchedule.TravelDates;
import com.lulobank.otp.services.v3.domain.clienttravels.CreateTravelSchedule.TravelDestination;
import com.lulobank.otp.services.v3.domain.clienttravels.CreateTravelSchedule.TravelRegion;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CreateTravelScheduleTest {

  private CreateTravelSchedule clientTravel1;
  private CreateTravelSchedule clientTravel2;

  @Before
  public void init() {
    clientTravel1 = buildClientTravel();
    clientTravel2 = buildClientTravel();
  }

  @Test
  public void equalityOnSameValuesInstances() {
    assertEquals(clientTravel1, clientTravel2);
  }

  @Test
  public void hashCodeOnSameValuesInstances() {
    assertEquals(clientTravel1.hashCode(), clientTravel2.hashCode());
  }

  private CreateTravelSchedule buildClientTravel() {
    CreateTravelSchedule createTravelSchedule = new CreateTravelSchedule();
    createTravelSchedule.setTravelName("travel test");

    TravelContactInfo contactInfo = new TravelContactInfo();
    contactInfo.setEmailAddress("contact@email.com");
    contactInfo.setName("contact BFF");
    contactInfo.setPhone("3001234567");
    createTravelSchedule.setContactInfo(contactInfo);

    TravelDates dates = new TravelDates();
    dates.setFrom("2019-12-15");
    dates.setTo("2020-01-15");

    TravelRegion region = new TravelRegion();
    region.setName("South America");
    region.setCode("SA");
    region.setIconId("sa-icon");

    TravelCountry countryEquator = new TravelCountry();
    countryEquator.setName("Equator");
    countryEquator.setCode("EC");
    countryEquator.setIconId("ec-icon");
    countryEquator.setAlphaCode("SA");
    TravelCountry countryPeru = new TravelCountry();
    countryPeru.setName("Peru");
    countryPeru.setCode("PE");
    countryPeru.setIconId("pe-icon");
    countryPeru.setAlphaCode("SA");

    TravelDestination destination = new TravelDestination();
    destination.setRegion(region);
    destination.setCountries(Arrays.asList(countryPeru, countryEquator));
    return createTravelSchedule;
  }

}
