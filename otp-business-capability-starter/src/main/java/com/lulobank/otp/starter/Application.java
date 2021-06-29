package com.lulobank.otp.starter;

import brave.sampler.Sampler;
import co.com.lulobank.tracing.sqs.EventProcessorConfig;
import com.lulobank.otp.starter.adapters.out.pushnotifications.PushNotificationsServiceConfig;
import com.lulobank.starter.errortracking.SentryConfigurationRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@EnableCaching
@SpringBootApplication
@EntityScan(basePackages = "com.lulobank.otp.services")
@ServletComponentScan
@ComponentScan(basePackages = {"com.lulobank.otp.starter.config",
        "com.lulobank.otp.services",
        "com.lulobank.otp.starter",
        "com.lulobank.logger.api"},
        basePackageClasses = {SentryConfigurationRunner.class, EventProcessorConfig.class})
@Import({PushNotificationsServiceConfig.class})
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Colombia"));
        Locale.setDefault(Locale.forLanguageTag("es_CO"));
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}