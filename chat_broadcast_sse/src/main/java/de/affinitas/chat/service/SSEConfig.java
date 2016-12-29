package de.affinitas.chat.service;

public class SSEConfig implements ServerSentEventConfig {

    private final String servletPathStream;
    private final String valuesTemplateGetStream;
    private final int port;

    private final String mqttBrokerUrl;
    private final String mqttUser;
    private final String mqttPassword;
    private final String mqttRootTopic;

    public SSEConfig() {
        port = 8081;
        servletPathStream = "/chat/receive/*";
        valuesTemplateGetStream = "/chat/receive/{channel_id}";
        mqttBrokerUrl = "tcp://localhost:61613";
        mqttUser = "admin";
        mqttPassword = "password";
        mqttRootTopic = "affinitas_chat/one_to_one";
    }

    @Override public int getPort() { return port; }
    @Override public String getValuesTemplateStream() { return valuesTemplateGetStream; }
    @Override public String getServletPathStream() { return servletPathStream; }
    @Override public String getMqttBrokerUrl() {
        return mqttBrokerUrl;
    }
    @Override public String getMqttUser() {
        return mqttUser;
    }
    @Override public String getMqttPassword() {
        return mqttPassword;
    }

    @Override
    public String getMqttRootTopic() {
        return mqttRootTopic;
    }

}
