package experiment;

public class Main {

    public static void main(String... args) {

        System.out.println("= Setting up the proxy (switch it off to see the effect without it) =================");
        System.setProperty("https.proxyHost", "ip-45-40-178-111.ip.secureserver.net"); // Any proxy that supports `HTTP CONNECT` would do
        System.setProperty("https.proxyPort", "8080");

        System.out.println("\n= ManualHttpsConnectionExample =======================");
        System.out.println("(This will finnish with the HTTP response '403 Forbidden' after connect and request as expected (since we don't send the auth credentials)");
        ManualHttpsConnectionExample.main();

        System.out.println("\n\n\n= PahoSubscribeExample ==============================");
        System.out.println("(This is expected to also end up with '403 Forbidden' (which would present itself as a NullPointerException :-( ) but it fails with a 'javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure'");
        PahoSubscribeExample.main();
    }
}
