package de.affinitas.chat.service;

import de.affinitas.chat.service.handler.Chat;
import de.affinitas.chat.service.listener.BroadcastChatListener;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextListener;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ChatService {

    public static void main(String[] args) {
        SpringApplication.run(ChatService.class, args);
    }

    @Bean public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }

    @Bean public ServletContextListener listener() {
        return new BroadcastChatListener();
    }

    @Bean public Chat chatResource() {
        return new Chat();
    }

}