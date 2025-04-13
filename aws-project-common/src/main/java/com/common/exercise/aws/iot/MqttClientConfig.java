package com.common.exercise.aws.iot;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

public class MqttClientConfig {
    public static final String CA_CERTIFICATE_PEM_CRT = "ca-certificate.pem.crt";
    public static final String PRIVATE_PRIVATE_PEM_KEY = "private-private.pem.key";
    private static MqttClientConnection connection;

    public static synchronized MqttClientConnection getConnection() {
        if (connection == null) {
            try {
                initConnection();
                connection.connect().get();
            } catch (Exception e) {
                throw new RuntimeException("Could not init connection with the MQ client", e);
            }
        }
        return connection;
    }

    private static synchronized void initConnection() throws IOException {
        try (InputStream certificateIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(CA_CERTIFICATE_PEM_CRT);
             InputStream privateKeyIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(PRIVATE_PRIVATE_PEM_KEY);) {
            ByteArrayOutputStream certificateOut = new ByteArrayOutputStream();
            ByteArrayOutputStream privateKeyOut = new ByteArrayOutputStream();

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = certificateIn.read(buffer)) != -1) {
                certificateOut.write(buffer, 0, bytesRead);
            }

            while ((bytesRead = privateKeyIn.read(buffer)) != -1) {
                privateKeyOut.write(buffer, 0, bytesRead);
            }
            connection = AwsIotMqttConnectionBuilder.newMtlsBuilder(
                            certificateOut.toByteArray(),
                            privateKeyOut.toByteArray())
                    .withClientId(UUID.randomUUID().toString())
                    .withTimeoutMs(100000)
                    .withProtocolOperationTimeoutMs(60000)
                    .withPort(8883)
                    .withEndpoint("a11d5i02gxb31-ats.iot.us-east-1.amazonaws.com")
                    .build();
        }

    }


}
