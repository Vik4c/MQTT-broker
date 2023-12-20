package lab7.publisher;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class App {

    public static void main(String[] args) throws InterruptedException {
        final String host = "d5c45c8a2d864f4996f3c75c5e889c8e.s1.eu.hivemq.cloud"; // use your host-name, it should look like '<alphanumeric>.s2.eu.hivemq.cloud'
        final String username = "Test1"; // your credentials
        final String password = "Test1234";
   
        // 1. create the client
        final Mqtt5Client client = Mqtt5Client.builder() 
               // .identifier("sensor-" + getMacAddress()) // use a unique identifier
                .serverHost(host) 
                .automaticReconnectWithDefaultConfig() // the client automatically reconnects
                .serverPort(8883) // this is the port of your cluster, for mqtt it is the default port 8883
                .sslWithDefaultConfig() // establish a secured connection to HiveMQ Cloud using TLS
                .build();
                

        // 2. connect the client
        client.toBlocking().connectWith()
                .simpleAuth() // using authentication, which is required for a secure connection
                .username(username) // use the username and password you just created
                .password(password.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .willPublish() // the last message, before the client disconnects
                    .topic("home/willM")
                    .payload("sensor gone".getBytes())
                    .applyWillPublish()
                .send();

        int i=0;
        // 3. simulate periodic publishing of sensor data
        while (i<6) {
            TimeUnit.MILLISECONDS.sleep(500);

            client.toBlocking().publishWith()
                    .topic("home/temperature1")
                    .payload(getTemperature())
                    .send();

            TimeUnit.MILLISECONDS.sleep(500);
            
            i++;
        }
    }
    
    private static byte[] getTemperature() {
        // simulate a temperature sensor with values between 1°C and 45°C
        final int temperature = ThreadLocalRandom.current().nextInt(1, 45);
        return (temperature + "°C").getBytes(StandardCharsets.UTF_8);
    }
}