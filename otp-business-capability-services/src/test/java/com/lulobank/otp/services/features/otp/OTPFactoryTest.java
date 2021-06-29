package com.lulobank.otp.services.features.otp;

import com.lulobank.otp.services.features.onboardingotp.otp.OTPFactory;
import io.vavr.CheckedFunction1;
import io.vavr.test.Arbitrary;
import io.vavr.test.CheckResult;
import io.vavr.test.Property;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;


public class OTPFactoryTest {

    @Test
    public void otpFactoryWithConcurrent500Calls() {
        IntStream intStream = IntStream.iterate(5, i -> i).limit(500);
        Set<String> otpSet = intStream.parallel()
                .mapToObj(OTPFactory::create)
                .peek(i -> System.out.println("otp: " + i))
                .collect(Collectors.toSet());
        assertThat(otpSet.size(), is(equalTo(500)));
        System.out.println(otpSet.size());

    }

    @Test
    public void otpFactoryWithConcurrentCallsOverDefaultLimit() {
        int maxSize = OTPFactory.DEFAULT_LIMIT_CONCURRENT + 500;
        IntStream intStream = IntStream.iterate(5, i -> i).limit(maxSize);
        Set<String> otpSet = intStream.parallel()
                .mapToObj(OTPFactory::create)
                .peek(i -> System.out.println("otp: " + i))
                .collect(Collectors.toSet());
        assertThat(otpSet.size() > 1400, is(true));
        System.out.println(otpSet.size());
    }

    @Test
    public void otpFactoryPropertyTesting() {
        Arbitrary<Integer> ints = Arbitrary.integer()
                .filter(i -> i < 15);
        CheckedFunction1<Integer, Boolean> otpLengthIsTheSameAsInput
                = i -> (i >= 0) ? OTPFactory.create(i).length() == i : OTPFactory.create(i).length() == 0;
        CheckResult result = Property
                .def("Every call should return an OTP with the same length of the input or zero if input is negative")
                .forAll(ints)
                .suchThat(otpLengthIsTheSameAsInput)
                .check(100, 0);
        assertThat(result.isSatisfied(), is(true));
    }


    @Test
    public void otpFactoryWhenDefault() {
        String s = OTPFactory.create();
        System.out.println(s);
        assertThat(s.length(), is(equalTo(4)));
    }


    @Test
    public void otpFactory_shouldReturnValues_thatDoesntStartWithOne() {
        IntStream intStream = IntStream.iterate(4, i -> i).limit(10);
        List<String> otpValues = intStream.parallel()
                .mapToObj(OTPFactory::create)
                .peek(i -> System.out.println("otp: " + i))
                .filter(e -> e.startsWith("1"))
                .collect(Collectors.toList());
        assertThat(otpValues.size() < 10, is(true));
    }

    @Test
    public void otpFactoryWhenCustom() {
        String s = OTPFactory.create(6);
        System.out.println(s);
        assertThat(s.length(), is(equalTo(6)));
    }

    @Test
    public void otpFactoryWhenCustomZero() {
        String s = OTPFactory.create(0);
        System.out.println(s);
        assertThat(s.length(), is(equalTo(0)));
    }

    @Test
    public void otpFactoryWhenCustomNegative() {
        String s = OTPFactory.create(-1);
        System.out.println(s);
        assertThat(s.length(), is(equalTo(0)));
    }


    @Test
    public void otpFactoryWithoutConcurrentCalls() {
        String s1 = OTPFactory.create(5);
        String s2 = OTPFactory.create(5);
        String s3 = OTPFactory.create(5);
        Set<String> otpSet = Stream.of(s1, s2, s3).collect(Collectors.toSet());
        assertThat(otpSet.size(), is(equalTo(3)));
    }


}
