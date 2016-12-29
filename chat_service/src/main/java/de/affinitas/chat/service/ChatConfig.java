package de.affinitas.chat.service;

public class ChatConfig {

    private String mqttRootTopic;
    private String mqttBrokerUrl;
    private String mqttUser;
    private String mqttPassword;

    public ChatConfig() {
        mqttRootTopic = "affinitas_chat/one_to_one";
        mqttBrokerUrl = "tcp://localhost:61613";
        mqttUser = "admin";
        mqttPassword = "password";
    }

    public String getMqttRootTopic() {
        return mqttRootTopic;
    }

    public String getMqttBrokerUrl() {
        return mqttBrokerUrl;
    }

    public String getMqttUser() {
        return mqttUser;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }
}
