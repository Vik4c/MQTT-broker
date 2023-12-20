package lab7.subscriber;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient.Mqtt5Publishes;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
class App
{
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
        .send();





if(client.getState().isConnected())
{
	  client.toBlocking().subscribeWith().topicFilter("home/willM").qos(MqttQos.EXACTLY_ONCE).send();
	  client.toBlocking().subscribeWith().topicFilter("home/temperature1").qos(MqttQos.EXACTLY_ONCE).send();

		  
	  try (Mqtt5Publishes publishes =  client.toBlocking().publishes(MqttGlobalPublishFilter.ALL)) {
		   while(true) {
		  Mqtt5Publish publishMessage1 = publishes.receive();

		  if(publishMessage1 == null) {
		    	System.out.println("No message has been recieved in the expected time");
		    	continue;
		    }
		  
		    String topic = publishMessage1.getTopic().toString();
		    byte [] payloadBytes = publishMessage1.getPayloadAsBytes();
		    String payload = new String(payloadBytes, StandardCharsets.UTF_8);
		    int temperature = Integer.parseInt(payload.replaceAll("[^0-9]", ""));
		    
		    if(topic.equals("home/willM"))
		    	System.out.println(new String(publishMessage1.getPayloadAsBytes()));
		    
//		    if(topic.equals("home/temperature1"))
//		    	processTemperatureMessage(publishMessage1.getPayloadAsBytes());
		    
		    if(topic.equals("home/temperature1")) {
		    	System.out.println("Recieved temperature: " + payload);
		    	
		    	System.out.print("+".repeat(temperature));
		        System.out.println(" " + temperature);
		        if(temperature>38) 
		        	System.out.println("Mnogu visoka temperatura");
		        else
		        	System.out.println();
		    }
		    
		    
		   } 
	  } catch (Exception e) {
		  e.printStackTrace();
		} 
	  
}
else
{
	  System.out.print("MQTT service connect failed");
}

}
//    public static void processTemperatureMessage(byte [] payloadBytes) {
//    	String payload = new String(payloadBytes, StandardCharsets.UTF_8);
//    	System.out.println("Recieved temperature: " + payload);
//    	
//    	int temperature = Integer.parseInt(payload.replaceAll("[^0-9]", ""));
//    	printFormattedOutput(temperature);
//    }
//    

//    public static void printFormattedOutput(int temperature) {
//    	StringBuilder stars=new StringBuilder();
//    	for (int i=0; i< temperature; i++) {
//    		stars.append("+");
//    	}
//    	System.out.println(stars.toString()+ temperature);
//    	
//    	if(temperature>38) 
//    		System.out.println("Mnogu golema temperatura");
//    	else 
//    		System.out.println();  	
    	
////    	ili
    	
////    	System.out.print("+".repeat(temperature));
////        System.out.println(" " + temperature);
////        if(temperature>38) 
////        	System.out.println("Mnogu visoka temperatura");
////        else
////        	System.out.println();
    	}
    }