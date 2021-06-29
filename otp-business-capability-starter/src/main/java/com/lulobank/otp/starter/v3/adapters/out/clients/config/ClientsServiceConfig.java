package com.lulobank.otp.starter.v3.adapters.out.clients.config;

import com.lulobank.otp.services.v3.port.out.clients.ClientsPort;
import com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceAdapter;
import com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ClientsServiceConfig {

    @Value("${services.clients.url}")
    private String serviceDomain;

    @Bean
    public ClientsServiceClient getClientsServiceClient(){
        return new ClientsServiceClient(serviceDomain);
    }

    @Bean
    public ClientsPort getClientsPort(ClientsServiceClient clientsServiceClient){
        return new ClientsServiceAdapter(clientsServiceClient);
    }
}
