package experiment.tunnel;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.SocketChannel;

/**
 * An unconnected socket that, when its .connect is invoked, connects by tunneling through a HTTP(s) proxy
 * and then forwards all calls to the resulting socket.
 */
public class TunnelingSocketProxy extends SSLSocket {
    private SSLSocket socket;

    @Override
    public String[] getSupportedCipherSuites() {
        return socket.getSupportedCipherSuites();
    }

    @Override
    public String[] getEnabledCipherSuites() {
        return socket.getEnabledCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(String[] strings) {
        socket.setEnabledCipherSuites(strings);
    }

    @Override
    public String[] getSupportedProtocols() {
        return socket.getSupportedProtocols();
    }

    @Override
    public String[] getEnabledProtocols() {
        return socket.getEnabledProtocols();
    }

    @Override
    public void setEnabledProtocols(String[] strings) {
        socket.setEnabledProtocols(strings);
    }

    @Override
    public SSLSession getSession() {
        return socket.getSession();
    }

    @Override
    public SSLSession getHandshakeSession() {
        return socket.getHandshakeSession();
    }

    @Override
    public void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
        socket.addHandshakeCompletedListener(handshakeCompletedListener);
    }

    @Override
    public void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
        socket.removeHandshakeCompletedListener(handshakeCompletedListener);
    }

    @Override
    public void startHandshake() throws IOException {
        socket.startHandshake();
    }

    @Override
    public void setUseClientMode(boolean b) {
        socket.setUseClientMode(b);
    }

    @Override
    public boolean getUseClientMode() {
        return socket.getUseClientMode();
    }

    @Override
    public void setNeedClientAuth(boolean b) {
        socket.setNeedClientAuth(b);
    }

    @Override
    public boolean getNeedClientAuth() {
        return socket.getNeedClientAuth();
    }

    @Override
    public void setWantClientAuth(boolean b) {
        socket.setWantClientAuth(b);
    }

    @Override
    public boolean getWantClientAuth() {
        return socket.getWantClientAuth();
    }

    @Override
    public void setEnableSessionCreation(boolean b) {
        socket.setEnableSessionCreation(b);
    }

    @Override
    public boolean getEnableSessionCreation() {
        return socket.getEnableSessionCreation();
    }

    @Override
    public SSLParameters getSSLParameters() {
        return socket.getSSLParameters();
    }

    @Override
    public void setSSLParameters(SSLParameters sslParameters) {
        socket.setSSLParameters(sslParameters);
    }

    private SSLTunnelSocketFactory sslTunnelSocketFactory;

    public TunnelingSocketProxy(SSLTunnelSocketFactory sslTunnelSocketFactory) {
        this.sslTunnelSocketFactory = sslTunnelSocketFactory;
    }

    private Socket getSocket() {
        if (socket == null) {
            throw new IllegalStateException("You cannot access any method unless .connect has been invoked first.");
        }
        return socket;
    }

    @Override
    public void connect(SocketAddress socketAddress) throws IOException {
        connect(socketAddress, 0);
    }

    @Override
    public void connect(SocketAddress socketAddress, int timeout) throws IOException {
        System.out.println("TSF.connect: " + socketAddress);
        this.socket = (SSLSocket) sslTunnelSocketFactory.createSocket(
                null,
                ((InetSocketAddress) socketAddress).getHostName(),
                ((InetSocketAddress) socketAddress).getPort(),
                true,
                timeout
        );
    }

    public TunnelingSocketProxy() {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    public TunnelingSocketProxy(Proxy proxy) {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    public TunnelingSocketProxy(SocketImpl socket) throws SocketException {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    public TunnelingSocketProxy(String s, int i) throws IOException {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    public TunnelingSocketProxy(InetAddress inetAddress, int i) throws IOException {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    public TunnelingSocketProxy(String s, int i, InetAddress inetAddress, int i1) throws IOException {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    public TunnelingSocketProxy(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        throw new UnsupportedOperationException("Only the constructor taking an SSLTunnelSocketFactory is supported.");
    }

    //------------------------------------------------------------------------------------------------

    @Override
    public void bind(SocketAddress socketAddress) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InetAddress getInetAddress() {
        return getSocket().getInetAddress();
    }

    @Override
    public InetAddress getLocalAddress() {
        return getSocket().getLocalAddress();
    }

    @Override
    public int getPort() {
        return getSocket().getPort();
    }

    @Override
    public int getLocalPort() {
        return getSocket().getLocalPort();
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return getSocket().getRemoteSocketAddress();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return getSocket().getLocalSocketAddress();
    }

    @Override
    public SocketChannel getChannel() {
        return getSocket().getChannel();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getSocket().getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getSocket().getOutputStream();
    }

    @Override
    public void setTcpNoDelay(boolean b) throws SocketException {
        getSocket().setTcpNoDelay(b);
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return getSocket().getTcpNoDelay();
    }

    @Override
    public void setSoLinger(boolean b, int i) throws SocketException {
        getSocket().setSoLinger(b, i);
    }

    @Override
    public int getSoLinger() throws SocketException {
        return getSocket().getSoLinger();
    }

    @Override
    public void sendUrgentData(int i) throws IOException {
        getSocket().sendUrgentData(i);
    }

    @Override
    public void setOOBInline(boolean b) throws SocketException {
        getSocket().setOOBInline(b);
    }

    @Override
    public boolean getOOBInline() throws SocketException {
        return getSocket().getOOBInline();
    }

    @Override
    public void setSoTimeout(int i) throws SocketException {
        getSocket().setSoTimeout(i);
    }

    @Override
    public int getSoTimeout() throws SocketException {
        return getSocket().getSoTimeout();
    }

    @Override
    public void setSendBufferSize(int i) throws SocketException {
        getSocket().setSendBufferSize(i);
    }

    @Override
    public int getSendBufferSize() throws SocketException {
        return getSocket().getSendBufferSize();
    }

    @Override
    public void setReceiveBufferSize(int i) throws SocketException {
        getSocket().setReceiveBufferSize(i);
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
        return getSocket().getReceiveBufferSize();
    }

    @Override
    public void setKeepAlive(boolean b) throws SocketException {
        getSocket().setKeepAlive(b);
    }

    @Override
    public boolean getKeepAlive() throws SocketException {
        return getSocket().getKeepAlive();
    }

    @Override
    public void setTrafficClass(int i) throws SocketException {
        getSocket().setTrafficClass(i);
    }

    @Override
    public int getTrafficClass() throws SocketException {
        return getSocket().getTrafficClass();
    }

    @Override
    public void setReuseAddress(boolean b) throws SocketException {
        getSocket().setReuseAddress(b);
    }

    @Override
    public boolean getReuseAddress() throws SocketException {
        return getSocket().getReuseAddress();
    }

    @Override
    public void close() throws IOException {
        getSocket().close();
    }

    @Override
    public void shutdownInput() throws IOException {
        getSocket().shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        getSocket().shutdownOutput();
    }

    @Override
    public String toString() {
        return getSocket().toString();
    }

    @Override
    public boolean isConnected() {
        return getSocket().isConnected();
    }

    @Override
    public boolean isBound() {
        return getSocket().isBound();
    }

    @Override
    public boolean isClosed() {
        return getSocket().isClosed();
    }

    @Override
    public boolean isInputShutdown() {
        return getSocket().isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        return getSocket().isOutputShutdown();
    }

    @Override
    public void setPerformancePreferences(int i, int i1, int i2) {
        getSocket().setPerformancePreferences(i, i1, i2);
    }
}
