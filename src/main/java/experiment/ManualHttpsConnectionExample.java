package experiment;

import experiment.tunnel.SSLTunnelSocketFactory;
import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ManualHttpsConnectionExample {

    public static void main(String... args) {
        try {
            Socket socket = SSLTunnelSocketFactory.getInstance().createSocket();
            System.out.println("Connecting...");
            socket.connect(new InetSocketAddress("a1kwih0squysgn.iot.eu-west-1.amazonaws.com", 443));

            System.out.println("Connected...");

            String[] reqLines = {
                    "GET /mqtt" + " HTTP/1.1",
                    "Host: a1kwih0squysgn.iot.eu-west-1.amazonaws.com",
                    "Upgrade: websocket",
                    "Connection: Upgrade",
                    "Sec-WebSocket-Key: " + (Base64.encode("mqtt-" + (System.currentTimeMillis() / 1000))),
                    "Sec-WebSocket-Protocol: mqttv3.1",
                    "Sec-WebSocket-Version: 13",
                    ""};

            String req = Arrays.stream(reqLines).map(v -> v + "\r\n").collect(Collectors.joining());
            System.out.println(req);

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.print(req);
            writer.flush();

            System.out.println("REQ sent. Response (expected to get 403 Forbidden):");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(reader.lines().collect(Collectors.joining("\n")));


        } catch (Exception e) {
            System.err.println("FAILED: " + e);
            e.printStackTrace();
        }
    }
}
