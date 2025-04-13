package com.common.exercise.aws.iot;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

public class AwsIotServiceClient {
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> void publishDataToIotCore(T payload, String topic) {
        MqttClientConnection connection = MqttClientConfig.getConnection();
        String data;
        try {
            data = mapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialized" + payload + " payload to IoT core ", e);
        }

        MqttMessage mqttMessage = new MqttMessage(topic,
                data.getBytes(StandardCharsets.UTF_8),
                QualityOfService.AT_LEAST_ONCE);
        CompletableFuture<Integer> response = connection.publish(mqttMessage);
        response.thenAccept(result -> {
            System.out.println("Result: " + result);
        });

    }
}
