package financialanalyzer.stockhistory;

import com.opencsv.CSVReader;
import financialanalyzer.config.AppConfig;
import financialanalyzer.http.HTMLPage;
import financialanalyzer.http.HttpFetcher;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class NasDaqStockHistoryProvider implements StockHistoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NasDaqStockHistoryProvider.class.getName());

    private static final String IDENTIFIER = "nasdaq";

    @Autowired
    protected AppConfig appConfig;

    @Autowired
    protected HttpFetcher httpFetcher;

    @Override
    public List<StockHistory> getStockHistoryForCompany(String _exchange, String _symbol) {
        return this.getStockHistoryForCompanyForDay(_exchange, _symbol, null);
    }

    @Override
    public List<StockHistory> getStockHistoryForCompanyForDay(String _exchange, String _symbol, Date _date) {
        return this.downloadAndProcessCSVFromNasDaq(_exchange, _symbol, _date);
    }

    protected HTMLPage fetchPage(String _url) {
        return this.httpFetcher.getResponse(_url, true);
    }

    protected List<StockHistory> downloadAndProcessCSVFromNasDaq(String _exchange, String _symbol, Date _date) {
        //https://www.nasdaq.com/api/v1/historical/BA/stocks/2020-03-01/2020-03-07
        String url = "https://www.nasdaq.com/api/v1/historical/::SYMBOL::/stocks/::MIN-DATE::/::MAX-DATE::";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date refDate = null;
        if (_date != null) {
            refDate = _date;
        } else {
            refDate = new Date();
        }

        String min_date = "";
        String max_date = "";
        if (refDate != null) {
            max_date = sdf.format(refDate);
            Calendar now = Calendar.getInstance();
            now.setTime(refDate);
            now.add(Calendar.DAY_OF_YEAR, -10);
            min_date = sdf.format(now.getTime());
        }

        String resolvedURL = url.replaceAll("::SYMBOL::", _symbol).replaceAll("::MIN-DATE::", min_date).replaceAll("::MAX-DATE::", max_date);
        String downloadDirectoryPath = this.appConfig.getStockHistoryDownloadDir() + File.separator + sdf.format(new Date());
        File downloadDirecory = new File(downloadDirectoryPath);
        downloadDirecory.mkdirs();
        String downloadFileName = downloadDirectoryPath + File.separator + _symbol + "-" + max_date + ".csv";

        File downloadFile = new File(downloadFileName);
        if (downloadFile.exists()) {
            return null;
        }

        boolean downloaded = false;
        int retryCounter = 0;
        while (!downloaded && retryCounter < 3) {
            LOGGER.info("Download Attempt:" + retryCounter);
            downloaded = this.httpFetcher.downloadToFile(resolvedURL, downloadFileName);
            retryCounter++;

        }

//boolean downloaded = this.downloadCSVForExchangeFromNasDaq(resolvedURL, downloadFile);
        LOGGER.info(downloaded + " : " + resolvedURL);
        if (downloaded) {
            List<StockHistory> shs = new ArrayList<>();
            shs = processStockHistoryExchangeCVS(_exchange, _symbol, _date, downloadFileName);
            File downloadedFile = new File(downloadFileName);
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
            return shs;
        }
        return null;
    }

    protected boolean downloadCSVForExchangeFromNasDaq(String _url, String _fileName) {

        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

// Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }

        try {
            URL csvUrl = new URL(_url);
            HttpURLConnection urlConnection = (HttpURLConnection) csvUrl.openConnection();
            //HttpURLConnection httpUrlConneciton = csvUrl.openopenConnection();
            //BufferedReader in = new BufferedReader(new InputStreamReader(csvUrl.openStream()));
            //String inputLine;
            //while ((inputLine = in.readLine()) != null) {
            //    System.out.println(inputLine);
            //}

            //in.close();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(2000);
            //if (_agent != null) {
            urlConnection.addRequestProperty("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/80.0.3987.163 Chrome/80.0.3987.163 Safari/537.36");

            InputStream in = urlConnection.getInputStream();
            //Files.copy(in, Paths.get(_fileName), StandardCopyOption.REPLACE_EXISTING);
            BufferedInputStream bis = new BufferedInputStream(in);
            FileOutputStream fos = new FileOutputStream(_fileName);

            byte data[] = new byte[1024];
            int count;
            while ((count = bis.read(data, 0, 1024)) != -1) {
                fos.write(data, 0, count);
            }

            File f = new File(_fileName);
            if (f.canRead()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    protected List<StockHistory> processStockHistoryExchangeCVS(String _exchange, String _symbol, Date _date, String _filename) {
        List<StockHistory> shs = new ArrayList<>();
        CSVReader reader = null;
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");

        try {
            reader = new CSVReader(new FileReader(_filename));

            String[] line;
            int lineCounter = 0;
            while ((line = reader.readNext()) != null) {
                if (lineCounter != 0) {
                    StockHistory sh = new StockHistory();
                    sh.setSymbol(_symbol);
                    sh.setExchange(_exchange);

                    sh.setRecordDate(sdf.parse(line[0]));
                    sh.setOpen(Float.parseFloat(line[3].replaceAll("\\$", "")));
                    sh.setClose(Float.parseFloat(line[1].replaceAll("\\$", "")));
                    sh.setHigh(Float.parseFloat(line[4].replaceAll("\\$", "")));
                    sh.setLow(Float.parseFloat(line[5].replaceAll("\\$", "")));
                    if (!line[2].contains("N/A")) {
                        sh.setVolume(Integer.parseInt(line[2].replaceAll(" ", "")));

                    }
                    sh.setActual_gain(sh.getClose() - sh.getOpen());
                    sh.setPercent_gain(sh.getActual_gain() / sh.getOpen());
                    shs.add(sh);
                }
                lineCounter++;
                //System.out.println("Country [id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
            }
        } catch (ParseException e) {
            LOGGER.error("Unable to parse date in csv:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {

            LOGGER.error("Unable to process csv:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error(_symbol + " : Unable to process csv:" + e.getMessage());
            e.printStackTrace();
        }

        return shs;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

}
