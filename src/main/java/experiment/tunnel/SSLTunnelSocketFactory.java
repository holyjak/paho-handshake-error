/**************************************************************************
 * http://www.javaworld.com/javaworld/javatips/jw-javatip111.html
 * http://www.bytemine.net/
 *
 * SOURCE: https://github.com/bytemine/bytemine-manager/blob/master/src/net/bytemine/manager/update/SSLTunnelSocketFactory.java
 *
 * LICENSE:
 *
 * Copyright (c) 2009 - 2013, bytemine GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *************************************************************************/

package experiment.tunnel;

import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * SSLSocketFactory for tunneling sslsockets through a proxy
 */
public class SSLTunnelSocketFactory extends SSLSocketFactory {

    private static class TrustEveryoneManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1){
            System.out.println("TSF: >>>>>>> TrustEveryoneManager.checkClientTrusted");
        }
        public void checkServerTrusted(X509Certificate[] arg0, String arg1){
            System.out.println("TSF: >>>>>>> TrustEveryoneManager.checkServerTrusted");
        }
        public X509Certificate[] getAcceptedIssuers() {
            System.out.println("TSF: >>>>>>> TrustEveryoneManager.getAcceptedIssuers");
            return null;
        }
    }

    public static class AcceptAllProvider extends java.security.Provider {
        private static final long serialVersionUID = 1L;

        public AcceptAllProvider() {
            super("AcceptAllProvider", 1.0, "Trust all X509 certificates");
            put("TrustManagerFactory.TrustAllCertificates", AcceptAllTrustManagerFactory.class.getName());
        }
    }
    protected static class AcceptAllTrustManagerFactory extends javax.net.ssl.TrustManagerFactorySpi {
        public AcceptAllTrustManagerFactory() {}
        protected void engineInit(java.security.KeyStore keystore) {}

        protected void engineInit(javax.net.ssl.ManagerFactoryParameters parameters) {}

        protected javax.net.ssl.TrustManager[] engineGetTrustManagers() {
            return new javax.net.ssl.TrustManager[]{new AcceptAllX509TrustManager()};
        }
    }
    protected static class AcceptAllX509TrustManager implements javax.net.ssl.X509TrustManager {
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certificateChain,
                String authType) throws java.security.cert.CertificateException {
            report("Client authtype=" + authType);
            for (java.security.cert.X509Certificate certificate : certificateChain) {
                report("Accepting:" + certificate);
            }
        }
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certificateChain,
                String authType) throws java.security.cert.CertificateException {
            report("Server authtype=" + authType);
            for (java.security.cert.X509Certificate certificate : certificateChain) {
                report("Accepting:" + certificate);
            }
        }
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
        private static void report(String string) {
            System.out.println(">>>>>>>" + string);
        }
    }

    private static Logger logger = Logger.getLogger(SSLTunnelSocketFactory.class.getName());

    private SSLSocketFactory dfactory;

    private String tunnelHost;

    private int tunnelPort;

    public static SocketFactory getInstance() {
        return getInstance(null);
    }

    /**
     * Return a SocketFactory - the tunneling one of https.proxyHost/Port are set, the default one otherwise.
     */
    public static SocketFactory getInstance(Properties sslClientProps) {
        String proxyHost = System.getProperty("https.proxyHost");
        Integer proxyPort = Integer.getInteger("https.proxyPort");

        if (sslClientProps == null) {
            sslClientProps = new Properties();
        }

        java.security.Security.addProvider(new AcceptAllProvider());

        sslClientProps.setProperty(SSLSocketFactoryFactory.TRUSTSTOREMGR, "TrustAllCertificates");
        sslClientProps.setProperty(SSLSocketFactoryFactory.TRUSTSTOREPROVIDER, "AcceptAllProvider");
        System.out.println("TSF: SSLTunnelSocketFactory: Set the trust all manager");



        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { new TrustEveryoneManager() }, null);
            SSLSocketFactory defaultFactory = sslContext.getSocketFactory();


//            // Adapted from MqttAsyncClient:
//            // (Note: SSLSocketFactory.getDefault() doesn't work b/c Paho calls the no-arg .createSocket(),
//            // which isn't implemented.)
//            SSLSocketFactoryFactory wSSFactoryFactory = new SSLSocketFactoryFactory();
//            if (null != sslClientProps)
//                wSSFactoryFactory.initialize(sslClientProps, null);
//            SSLSocketFactory defaultFactory = wSSFactoryFactory.createSocketFactory(null);

            if (proxyHost != null && proxyPort != null) {
                System.out.println("TSF: SSLTunnelSocketFactory: Proxy detected, creating an instance");
                return new SSLTunnelSocketFactory(defaultFactory, proxyHost, proxyPort);
            } else {
                System.out.println("TSF: SSLTunnelSocketFactory: NO proxy detected, returning the default factory");
                return defaultFactory;
            }
//        } catch (MqttSecurityException e) {
//            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SSLTunnelSocketFactory(SSLSocketFactory defaultFactory, String proxyhost, int proxyport) {
        tunnelHost = proxyhost;
        tunnelPort = proxyport;
        dfactory = defaultFactory;
    }

    @Override
    public Socket createSocket() throws IOException {
        return new TunnelingSocketProxy(this);
    }

    public Socket createSocket(String host, int port) throws IOException,
            UnknownHostException {
        return createSocket(null, host, port, true);
    }

    public Socket createSocket(String host, int port, InetAddress clientHost,
                               int clientPort) throws IOException, UnknownHostException {
        return createSocket(null, host, port, true);
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return createSocket(null, host.getHostName(), port, true);
    }

    public Socket createSocket(InetAddress address, int port,
                               InetAddress clientAddress, int clientPort) throws IOException {
        return createSocket(null, address.getHostName(), port, true);
    }

    public Socket createSocket(Socket s, String host, int port,
                               boolean autoClose) throws IOException {
        return createSocket(s, host, port, autoClose, 0);

    }

    public Socket createSocket(Socket s, String host, int port,
                               boolean autoClose, int timeout) throws IOException, UnknownHostException {

        System.out.println("TSF: createSocket " + host + ":" + port);

        Socket tunnel = new Socket();
        tunnel.connect(new InetSocketAddress(tunnelHost, tunnelPort), timeout);

        doTunnelHandshake(tunnel, host, port);

        SSLSocket result = (SSLSocket) dfactory.createSocket(
                tunnel, host, port, autoClose);

        result.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            public void handshakeCompleted(HandshakeCompletedEvent event) {
                logger.fine("Handshake finished!");
                logger.fine("\t CipherSuite:" + event.getCipherSuite());
                logger.fine("\t SessionId " + event.getSession());
                logger.fine("\t PeerHost "
                        + event.getSession().getPeerHost());
                System.out.println("TSF: Tunnelling Handshake with the external system (through the proxy) finished");
            }
        });

        result.startHandshake();

        return result;
    }

    private void doTunnelHandshake(Socket tunnel, String host, int port)
            throws IOException {
        OutputStream out = tunnel.getOutputStream();
        String msg = "CONNECT " + host + ":" + port + " HTTP/1.0\n"
                + "User-Agent: "
                + sun.net.www.protocol.http.HttpURLConnection.userAgent
                + "\r\n\r\n";
        byte b[];
        try {
            /*
                * We really do want ASCII7 -- the http protocol doesn't change
                * with locale.
                */
            b = msg.getBytes("ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            /*
                * If ASCII7 isn't there, something serious is wrong, but
                * Paranoia Is Good (tm)
                */
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();

        /*
           * We need to store the reply so we can create a detailed
           * error message to the user.
           */
        byte reply[] = new byte[200];
        int replyLen = 0;
        int newlinesSeen = 0;
        boolean headerDone = false; /* Done on first newline */

        InputStream in = tunnel.getInputStream();

        while (newlinesSeen < 2) {
            int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from proxy");
            }
            if (i == '\n') {
                headerDone = true;
                ++newlinesSeen;
            } else if (i != '\r') {
                newlinesSeen = 0;
                if (!headerDone && replyLen < reply.length) {
                    reply[replyLen++] = (byte) i;
                }
            }
        }

        /*
           * Converting the byte array to a string is slightly wasteful
           * in the case where the connection was successful, but it's
           * insignificant compared to the network overhead.
           */
        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            replyStr = new String(reply, 0, replyLen);
        }

        /* Look for 200 connection established */
        System.out.println("TSF: Tunnel response to HTTP CONNECT: " + replyStr);
        if (replyStr.toLowerCase().indexOf("200 connection established") == -1) {
            throw new IOException("Unable to tunnel through " + tunnelHost
                    + ":" + tunnelPort + ".  Proxy returns \"" + replyStr
                    + "\"");
        }

        /* tunneling Handshake was successful! */
    }

    public String[] getDefaultCipherSuites() {
        return dfactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return dfactory.getSupportedCipherSuites();
    }
}