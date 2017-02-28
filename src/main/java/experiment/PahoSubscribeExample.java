package experiment;

import experiment.tunnel.SSLTunnelSocketFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.TimeUnit;

public class PahoSubscribeExample {

    public static void main(String... args) {
        String broker       = "wss://a1kwih0squysgn.iot.eu-west-1.amazonaws.com:443";
        String clientId     = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            connOpts.setConnectionTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connOpts.setSocketFactory(SSLTunnelSocketFactory.getInstance());

            sampleClient.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("*connectionLost*: " + cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("*messageArrived*: " + topic + ": " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("*deliveryComplete*: " + token);
                }
            });

            System.out.println("Connecting to broker: "+broker +
                    "Expected to fail with NPE b/c there is no upgrade header (due to 403 Forbidden)");
            sampleClient.connect(connOpts);
            System.out.println("Connected");

//            sampleClient.subscribe(topic, 1);

//            System.out.println("Publishing message: "+content);
//            MqttMessage message = new MqttMessage(content.getBytes());
//            message.setQos(qos);
//            sampleClient.publish(topic, message);
//            System.out.println("Message published");

//            TimeUnit.SECONDS.sleep(3);
//            sampleClient.disconnect();
//            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        } catch (Exception e) {
            System.exit(7);
        }
    }
}