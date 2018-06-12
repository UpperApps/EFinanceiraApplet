package src.main.java.br.com.previ.applet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



/**
 * @author prvc23
 *
 */
public final class TrustModifier {

	private static X509Certificate certificate;  
	private static PrivateKey privateKey;   

	private TrustModifier(){

	}
	
	private static final TrustingHostnameVerifier 
	TRUSTING_HOSTNAME_VERIFIER = new TrustingHostnameVerifier();

	private static SSLSocketFactory factory;

	/** Call this with any HttpURLConnection, and it will 
    modify the trust settings if it is an HTTPS connection. */
	/**
	 * @param conn the connection to set
	 * @param keystore 
	 * @param chavePrivada 
	 * @throws KeyManagementException erro KeyManagementException
	 * @throws NoSuchAlgorithmException erro NoSuchAlgorithmException
	 * @throws KeyStoreException erro KeyStoreException
	 * @throws IOException 
	 * @throws CertificateException 
	 */
	public static void relaxHostChecking(HttpURLConnection conn, PrivateKey chavePrivada, X509Certificate certificado) 
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		
		certificate = certificado;
		privateKey = chavePrivada;
		
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConnection = (HttpsURLConnection) conn;
			SSLSocketFactory factory = prepFactory(httpsConnection);
			httpsConnection.setSSLSocketFactory(factory);
			httpsConnection.setHostnameVerifier(TRUSTING_HOSTNAME_VERIFIER);
		}
	}

	/**
	 * @param httpsConnection the httpsConnection to set
	 * @return fabrica 
	 * @throws NoSuchAlgorithmException erro NoSuchAlgorithmException
	 * @throws KeyStoreException erro KeyStoreException
	 * @throws KeyManagementException erro KeyManagementException
	 */
	static synchronized SSLSocketFactory prepFactory(HttpsURLConnection httpsConnection) 
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		if (factory == null) {
			KeyManager[] keyManagers = createKeyManagers();  
              
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(keyManagers, new TrustManager[]{ new AlwaysTrustManager() }, null);
			factory = ctx.getSocketFactory();
		}
		return factory;
	}

	/**
	 * @author prvc23
	 *
	 */
	private static final class TrustingHostnameVerifier implements
	HostnameVerifier {
		/**
		 * @param hostname the hostname to set
		 * @param session the session to set
		 * @return true
		 */
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * @author prvc23
	 *
	 */
	private static class AlwaysTrustManager implements X509TrustManager {
		/**
		 * @param arg0 the arg0 to set
		 * @param arg1 the arg1 to set
		 * @throws CertificateException erro CertificateException
		 */
		// checkClientTrusted
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
		/**
		 * @param arg0 the arg0 to set
		 * @param arg1 the arg1 to set
		 * @throws CertificateException erro CertificateException
		 */
		// checkServerTrusted
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
		/**
		 *@return null
		 */
		public X509Certificate[] getAcceptedIssuers() { return null; }      
	}

	// --- //

	public static KeyManager[] createKeyManagers() {  
		HSKeyManager keyManager = new HSKeyManager(certificate, privateKey);  
		return new KeyManager[] { keyManager };
	}  

	
}
