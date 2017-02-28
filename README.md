Demonstration of Paho breaking SSL, leading to `handshake_failure`
==================================================================

Demonstration of [an issue](https://github.com/eclipse/paho.mqtt.java/issues/335) when using a `HTTP CONNECT` proxy with Paho and a custom SocketFactory. While it is possible
to use the factory to connect to the MQTT server manually without a problem, when Paho does it, it fails with a
`javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure`.

See `experiment.Main` for details and run it via:

    mvn compile; mvn exec:java -Dexec.mainClass="experiment.Main"

You will see this output (first the successful manual connection (successful = it gets through, gets 403 from the server), 
than the failing Paho (failing = gets handshake failure, not a response from the server)):


```
= Setting up the proxy (switch it off to see the effect without it) =================

= ManualHttpsConnectionExample =======================
(This will finnish with the HTTP response '403 Forbidden' after connect and request as expected (since we don't send the auth credentials)
TSF: SSLTunnelSocketFactory: Set the trust all manager
TSF: SSLTunnelSocketFactory: Proxy detected, creating an instance
Connecting...
TSF.connect: a1kwih0squysgn.iot.eu-west-1.amazonaws.com/52.50.28.141:443
TSF: createSocket a1kwih0squysgn.iot.eu-west-1.amazonaws.com:443
TSF: Tunnel response to HTTP CONNECT: HTTP/1.1 200 Connection established
TSF: >>>>>>> TrustEveryoneManager.checkServerTrusted
TSF: >>>>>>> TrustEveryoneManager.getAcceptedIssuers
Connected...
TSF: Tunnelling Handshake with the external system (through the proxy) finished
GET /mqtt HTTP/1.1
Host: a1kwih0squysgn.iot.eu-west-1.amazonaws.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: bXF0dC0xNDg4MjgxNzMz
Sec-WebSocket-Protocol: mqttv3.1
Sec-WebSocket-Version: 13


REQ sent. Response (expected to get 403 Forbidden):
HTTP/1.1 403 Forbidden
content-type: application/json
content-length: 91
date: Tue, 28 Feb 2017 11:35:33 GMT
x-amzn-RequestId: e0d6f06a-1043-ced6-a76d-817d22c40f86
connection: Keep-Alive
x-amzn-ErrorType: ForbiddenException:

{"message":"Missing Authentication Token","traceId":"e0d6f06a-1043-ced6-a76d-817d22c40f86"}



= PahoSubscribeExample ==============================
(This is expected to also end up with '403 Forbidden' (which would present itself as a NullPointerException :-( ) but it fails with a 'javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure'
TSF: SSLTunnelSocketFactory: Set the trust all manager
TSF: SSLTunnelSocketFactory: Proxy detected, creating an instance
Connecting to broker: wss://a1kwih0squysgn.iot.eu-west-1.amazonaws.com:443Expected to fail with NPE b/c there is no upgrade header (due to 403 Forbidden)
TSF.connect: a1kwih0squysgn.iot.eu-west-1.amazonaws.com/52.50.28.141:443
TSF: createSocket a1kwih0squysgn.iot.eu-west-1.amazonaws.com:443
TSF: Tunnel response to HTTP CONNECT: HTTP/1.1 200 Connection established
TSF: >>>>>>> TrustEveryoneManager.checkServerTrusted
TSF: >>>>>>> TrustEveryoneManager.getAcceptedIssuers
TSF: Tunnelling Handshake with the external system (through the proxy) finished
TSF.connect: a1kwih0squysgn.iot.eu-west-1.amazonaws.com/52.50.28.141:443
TSF: createSocket a1kwih0squysgn.iot.eu-west-1.amazonaws.com:443
TSF: Tunnel response to HTTP CONNECT: HTTP/1.1 200 Connection established
TSF: >>>>>>> TrustEveryoneManager.checkServerTrusted
TSF: >>>>>>> TrustEveryoneManager.getAcceptedIssuers
TSF: Tunnelling Handshake with the external system (through the proxy) finished
reason 0
msg MqttException
loc MqttException
cause javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
excep MqttException (0) - javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
MqttException (0) - javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
	at org.eclipse.paho.client.mqttv3.internal.ExceptionHelper.createMqttException(ExceptionHelper.java:38)
	at org.eclipse.paho.client.mqttv3.internal.ClientComms$ConnectBG.run(ClientComms.java:664)
	at java.lang.Thread.run(Thread.java:745)
Caused by: javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
	at sun.security.ssl.Alerts.getSSLException(Alerts.java:192)
	at sun.security.ssl.Alerts.getSSLException(Alerts.java:154)
	at sun.security.ssl.SSLSocketImpl.recvAlert(SSLSocketImpl.java:2023)
	at sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:1125)
	at sun.security.ssl.SSLSocketImpl.readDataRecord(SSLSocketImpl.java:930)
	at sun.security.ssl.AppInputStream.read(AppInputStream.java:105)
	at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)
	at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)
	at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)
	at java.io.InputStreamReader.read(InputStreamReader.java:184)
	at java.io.BufferedReader.fill(BufferedReader.java:161)
	at java.io.BufferedReader.readLine(BufferedReader.java:324)
	at java.io.BufferedReader.readLine(BufferedReader.java:389)
	at org.eclipse.paho.client.mqttv3.internal.websocket.WebSocketHandshake.receiveHandshakeResponse(WebSocketHandshake.java:117)
	at org.eclipse.paho.client.mqttv3.internal.websocket.WebSocketHandshake.execute(WebSocketHandshake.java:74)
	at org.eclipse.paho.client.mqttv3.internal.websocket.WebSocketSecureNetworkModule.start(WebSocketSecureNetworkModule.java:77)
	at org.eclipse.paho.client.mqttv3.internal.ClientComms$ConnectBG.run(ClientComms.java:650)
	... 1 more
```
