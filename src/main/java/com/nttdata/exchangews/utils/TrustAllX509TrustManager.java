package com.nttdata.exchangews.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * DO NOT USE IN PRODUCTION!!!!
 * 
 * This class will simply trust everything that comes along.
 * 
 * @author frank
 *
 */
public class TrustAllX509TrustManager implements X509TrustManager {
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
			String authType) {
	}

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
			String authType) {
	}

	public static SSLContext trustEveryOne() throws NoSuchAlgorithmException,
	KeyManagementException {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[] { new TrustAllX509TrustManager() }, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
			@Override
			public boolean verify(String string,SSLSession ssls) {
				return true;
			}
		});
		return sc;
	}

}