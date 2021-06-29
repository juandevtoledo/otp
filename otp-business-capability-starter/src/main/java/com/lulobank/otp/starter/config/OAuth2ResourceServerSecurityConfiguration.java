package com.lulobank.otp.starter.config;

import com.lulobank.core.security.spring.LuloBankClaimsSetVerifier;
import com.lulobank.core.security.spring.LuloBankJwtDecoder;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Profile("!dev")
@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration extends WebSecurityConfigurerAdapter {
  public static final String SCOPE_PREFIX = "SCOPE_";

  @Value("${spring.security.oauth2.resourceserver.jwt.tenantAWS.jwk-set-uri}")
  String jwkSetUri;
  @Value("${spring.security.oauth2.resourceserver.jwt.tenantAWS.issuer}")
  String issuerAWS;
  @Value("${spring.security.oauth2.resourceserver.jwt.tenantAWS.authorized-scope}")
  String authorizedScopeAWS;
  @Value("${spring.security.oauth2.resourceserver.jwt.tenantAWS.authorized-scope-zendesk}")
  String authorizedScopeZendesk;
  @Value("${spring.security.oauth2.resourceserver.jwt.tenantLulo.issuer}")
  String issuerLulo;
  @Value("${spring.security.oauth2.resourceserver.jwt.tenantLulo.private-key-value}")
  RSAPrivateKey privateKey;
  @Value("${spring.security.oauth2.resourceserver.jwt.tenantLulo.public-key-value}")
  RSAPublicKey publicKey;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().cors().and() //TODO: Verificar como habilitar llamados desde swagger para volver a habilitar proteccion CSRF
      .authorizeRequests(authorizeRequests -> authorizeRequests.antMatchers("/swagger-ui/**").permitAll()
        .antMatchers("/swagger-resources/**").permitAll()
        .antMatchers("/webjars/springfox-swagger-ui/**").permitAll()
        .antMatchers("/**/api-docs").permitAll()
        .antMatchers("/info").permitAll()
        .antMatchers("/actuator/**").permitAll()
        .antMatchers("/info.json").permitAll()
        .antMatchers("/**/client/{idClient}/**").access("@webSecurity.checkClientId(authentication,#idClient)")
        .antMatchers("/generate/country/{\\s+}/phonenumber/{\\s+}").hasAuthority(SCOPE_PREFIX + authorizedScopeAWS)
        .antMatchers("/generate/email/{\\s+}").hasAuthority(SCOPE_PREFIX + authorizedScopeAWS)
        .antMatchers("/verify/country/{\\s+}/phonenumber/{\\s+}/token/{\\s+}").hasAuthority(SCOPE_PREFIX + authorizedScopeAWS)
        .antMatchers("/verify/email/{\\s+}/token/{\\s+}").hasAuthority(SCOPE_PREFIX + authorizedScopeAWS)
        .antMatchers("/authorization-sac").hasAuthority(SCOPE_PREFIX + authorizedScopeZendesk)
        .antMatchers("/verify/authorization-sac").hasAuthority(SCOPE_PREFIX + authorizedScopeZendesk)
        .antMatchers("/ivrInternal").hasAuthority(SCOPE_PREFIX + authorizedScopeZendesk)
        .antMatchers("/verify/ivrInternal").hasAuthority(SCOPE_PREFIX + authorizedScopeZendesk)
        .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.authenticationManagerResolver(multitenantAuthenticationManager()));
  }

  @Bean
  AuthenticationManagerResolver<HttpServletRequest> multitenantAuthenticationManager() {
    Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
    authenticationManagers.put(issuerAWS, AWS());
    authenticationManagers.put(issuerLulo, luloBank());
    return request -> {
      final BearerTokenResolver resolver = new DefaultBearerTokenResolver();
      String token = resolver.resolve(request);
      String tenantId = null;
      try {
        JWTClaimsSet claimSet = JWTParser.parse(token).getJWTClaimsSet();
        // TODO: Se puede hacer esto ya que solo tenemos dos tenants (lulo y cognito) pero se debe buscar la forma de dar con el claim del token encriptado
        tenantId = claimSet == null ? issuerLulo : claimSet.getIssuer();
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return Optional.ofNullable(tenantId)
        .map(authenticationManagers::get)
        .orElseThrow(() -> new IllegalArgumentException("unknown tenant"));
    };
  }

  AuthenticationManager AWS() {
    JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(new JwtBearerTokenAuthenticationConverter());
    return authenticationProvider::authenticate;
  }

  AuthenticationManager luloBank() {
    LuloBankJwtDecoder jwtDecoder = LuloBankJwtDecoder.withEncryptionKey(this.privateKey, this.publicKey)
      .build(new LuloBankClaimsSetVerifier());
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(new JwtBearerTokenAuthenticationConverter());
    return authenticationProvider::authenticate;
  }
}
