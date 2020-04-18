package financialanalyzer.stockhistory;

import com.opencsv.CSVReader;
import financialanalyzer.config.AppConfig;
import financialanalyzer.objects.StockHistory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class NasDaqStockHistoryProvider implements StockHistoryProvider {

    private static final Logger LOGGER = Logger.getLogger(NasDaqStockHistoryProvider.class.getName());

    @Autowired
    protected AppConfig appConfig;

    @Override
    public List<StockHistory> getStockHistoryForCompany(String _exchange, String _symbol) {
        return this.getStockHistoryForCompanyForDay(_exchange, _symbol, null);
    }

    @Override
    public List<StockHistory> getStockHistoryForCompanyForDay(String _exchange, String _symbol, Date _date) {
        return this.downloadAndProcessCSVFromNasDaq(_exchange, _symbol, _date);
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
            now.add(Calendar.DAY_OF_YEAR, -4);
            min_date = sdf.format(now.getTime());
        }

        String resolvedURL = url.replaceAll("::SYMBOL::", _symbol).replaceAll("::MIN-DATE::", min_date).replaceAll("::MAX-DATE::", max_date);
        String downloadDirectoryPath = this.appConfig.getStockHistoryDownloadDir() + "/" + sdf.format(new Date());
        File downloadDirecory = new File(downloadDirectoryPath);
        downloadDirecory.mkdirs();
        String downloadFile = downloadDirectoryPath + "/" + _symbol + "-" + max_date + ".csv";
        boolean downloaded = this.downloadCSVForExchangeFromNasDaq(resolvedURL, downloadFile);
        LOGGER.info(downloaded + " : " + resolvedURL);
        if (downloaded) {
            List<StockHistory> shs = new ArrayList<>();
            shs = processStockHistoryExchangeCVS(_exchange, _symbol, _date, downloadFile);
            return shs;
        }
        return null;
    }

    protected boolean downloadCSVForExchangeFromNasDaq(String _url, String _fileName) {
        try {
            URL csvUrl = new URL(_url);
            BufferedReader in = new BufferedReader(new InputStreamReader(csvUrl.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }

            in.close();

            //Files.copy(in., Paths.get(_fileName), StandardCopyOption.REPLACE_EXISTING);
            File f = new File(_fileName);
            if (f.canRead()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
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
            LOGGER.severe("Unable to parse date in csv:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {

            LOGGER.severe("Unable to process csv:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.severe(_symbol + " : Unable to process csv:" + e.getMessage());
            e.printStackTrace();
        }

        return shs;
    }
}
