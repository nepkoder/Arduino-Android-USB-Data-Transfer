package com.fazlagida.burak.smartscaleusb.Api;

import android.content.Context;

import com.fazlagida.burak.smartscaleusb.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String API = "https://sajiloemi.com/api/v1/";
    private static Retrofit retrofit = null;

    private static OkHttpClient.Builder httpClientBuilder = null;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {

            httpClientBuilder = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS);
            initHttpLogging();
            initSSL(context);

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientBuilder.build());


            retrofit = builder.build();

        }
        return retrofit;
    }


    private static void initHttpLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(logging);
    }

    private static void initSSL(Context context) {

        SSLContext sslContext = null;
        try {
            sslContext = createCertificate(context.getResources().openRawResource(R.raw.cert));
        } catch (CertificateException | IOException | KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (sslContext != null) {
            httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), systemDefaultTrustManager());
        }

    }

    private static SSLContext createCertificate(InputStream trustedCertificateIS) throws CertificateException, IOException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try {
            ca = cf.generateCertificate(trustedCertificateIS);
        } finally {
            trustedCertificateIS.close();
        }

        // creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // creating a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;

    }

    private static X509TrustManager systemDefaultTrustManager() {

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }

    }

}