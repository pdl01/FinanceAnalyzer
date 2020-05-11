package financialanalyzer.http;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class HttpFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFetcher.class.getName());
    private static final int BUFFER_SIZE = 4096;
    private static final String[] agents = {
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41"
    };
// Create a trust manager that does not validate certificate chains 
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }
    };

    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return oldFactory;
    }

    public boolean downloadToFile(String _url, String _fileName) {
        HttpURLConnection httpConn = null;
        try {
            URL url = new URL(_url);

            if (_url.startsWith("https")) {
                httpConn = (HttpsURLConnection) url.openConnection();
                HttpFetcher.trustAllHosts((HttpsURLConnection) httpConn);
            } else {
                httpConn = (HttpURLConnection) url.openConnection();
            }

            httpConn.setConnectTimeout(2000);
            httpConn.setReadTimeout(2000);
            httpConn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);

            httpConn.setRequestMethod("GET");
            httpConn.addRequestProperty("User-Agent", getRandomAgent());

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    //fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,fileURL.length());
                }
                /*
                LOGGER.info("Content-Type = " + contentType);
                LOGGER.info("Content-Disposition = " + disposition);
                LOGGER.info("Content-Length = " + contentLength);
                LOGGER.info("fileName = " + _fileName);
                */
                
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = _fileName;

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                return true;
            } else {
                LOGGER.error("Response:" + responseCode);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
                
            }
        }
        return false;
    }

    public HTMLPage getResponse(String _url, String _agent, boolean contentTextOnly) {
        try {
            URL urlObj = new URL(_url);
            HTMLPage htmlPage = new HTMLPage();
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            conn.setRequestMethod("GET");
            if (_agent != null) {
                conn.addRequestProperty("User-Agent", _agent);
            }
            int responseCode = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            htmlPage.setUrl(_url);
            htmlPage.setStatusCode(responseCode);
            String rawHTML = response.toString();
            Document doc = Jsoup.parse(rawHTML, "https://www.google.com");
            String title = doc.title();
            if (contentTextOnly) {
                htmlPage.setContent(doc.body().text());
            } else {
                htmlPage.setContent(rawHTML);
            }
            htmlPage.setTitle(title);
            return htmlPage;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    public HTMLPage getResponse(String _url, boolean contentTextOnly) {
        return this.getResponse(_url, this.getRandomAgent(), contentTextOnly);

    }

    private String getRandomAgent() {

        int index = (int) (Math.random() * agents.length);
        return agents[index];

    }
}
