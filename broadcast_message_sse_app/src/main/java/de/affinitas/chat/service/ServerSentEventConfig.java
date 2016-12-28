package de.affinitas.chat.service;

public interface ServerSentEventConfig {

    String getServletPathStream();

    int getPort();

    String getValuesTemplateStream();

    String getMqttBrokerUrl();

    String getMqttUser();

    String getMqttPassword();

    String getMqttRootTopic();
}