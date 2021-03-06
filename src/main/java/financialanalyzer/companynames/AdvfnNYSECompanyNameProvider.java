/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import static financialanalyzer.companynames.AdvfnAMEXCompanyNameProvider.IDENTIFIER;
import financialanalyzer.config.AppConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.stockhistory.StockHistory;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class AdvfnNYSECompanyNameProvider extends AbstractCompanyNameProvider implements CompanyNameProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvfnNYSECompanyNameProvider.class.getName());
    public static final String IDENTIFIER = "nyse";

    private static String download_url = "https://old.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
    //private static String latest_filename = AppConfig.companyDownloadDir + "/nyse_latest.csv";

    @Override
    public List<Company> getAllCompanies() {
        LOGGER.info("Starting getAllCompanies");
        this.downloadCSVForExchangeFromNasDaq(download_url, this.getLatestFileName());
        List<Company> companies = this.processCSVForExchangedFromNasDaq(this.getLatestFileName(), "nyse");

        LOGGER.info("Ending getAllCompanies");
        return companies;
    }

    @Override
    public List<Company> getCompaniesBeginningWithLetter(String _letter) {
        LOGGER.info("Starting getCompaniesBeginningWithLetter");
        LOGGER.info("Ending getCompaniesBeginningWithLetter");
        return null;
    }

    private String getLatestFileName() {
        return this.appConfig.getCompanyDownloadDir() + "/nyse_latest.csv";
    }
    /*
    @Override
    public List<StockHistory> getStockHistoryForCompany(String _symbol) {
        return this.getStockHistoryForCompanyForDay(_symbol,null);
    }

    @Override
    public List<StockHistory> getStockHistoryForCompanyForDay(String _symbol, Date _date) {
                return this.downloadAndProcessCSVFromNasDaq(CompanyNameProvider.EXCHANGE_NYSE,_symbol, _date);

        //return this.downloadTimeHistoryAlphavantage(CompanyProvider.EXCHANGE_NYSE,_symbol, _date);
    }
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }
}
