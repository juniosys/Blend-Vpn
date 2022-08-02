package com.slipkprojects.sockshttp.tunnel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.annotation.SuppressLint;

import org.conscrypt.Conscrypt;

import java.security.SecureRandom;


public class TLSSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory internalSSLSocketFactory;



    static {
        try {
            Security.insertProviderAt(Conscrypt.newProvider(), 1);

        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }}

    public SSLContext sslctx;

    public TLSSocketFactory(InputStream certStream) throws KeyManagementException, NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException {


        X509TrustManager tm = Conscrypt.getDefaultX509TrustManager();

        CertificateFactory cf = CertificateFactory.getInstance("Conscrypt");
        //(null, new TrustManager[] { tm }, null);


        //CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(certStream);
        try {
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("tm=" + ((X509Certificate) tm).getSubjectDN());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("tm", ca);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext sslctx = SSLContext.getInstance("TLS");
            //tentar ativar amanha
			//  SSLContext sslctx = SSLContext.getInstance("TLS", "Conscrypt");
            sslctx.init(null, tmf.getTrustManagers(), null);
            this.internalSSLSocketFactory = sslctx.getSocketFactory();
        } finally {
            caInput.close();
        }
    }


    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        // For easier debugging purpose, trust all certificates







        //dsp = ApplicationBase.getDefSharedPreferences();
        TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@SuppressLint({"TrustAllX509TrustManager"})
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				@SuppressLint({"TrustAllX509TrustManager"})
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
        };
        // SSLContext protocols: TLS, SSL, SSLv3
        //SSLContext sc = SSLContext.getInstance("SSLv3");
        //System.out.println("\nSSLContext class: "+sc.getClass());
        //System.out.println("   Protocol: "+sc.getProtocol());
        //System.out.println("   Provider: "+sc.getProvider());

        sslctx = SSLContext.getInstance("TLS");
        sslctx.init(null, trustAllCerts, new SecureRandom());
        internalSSLSocketFactory = sslctx.getSocketFactory();

    }

    @Override
    public String[] getDefaultCipherSuites() {
        return internalSSLSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return internalSSLSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    //	private Socket enableTLSOnSocket(Socket socket) {
//		if(socket instanceof SSLSocket) ((SSLSocket) socket).setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
//		return socket;
//
    private Socket enableTLSOnSocket(Socket socket) {

        if (socket instanceof SSLSocket) {
            ((SSLSocket) socket).setEnabledProtocols(((SSLSocket) socket).getSupportedProtocols());
            ((SSLSocket) socket).setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
        }
        return socket;
    }

}
