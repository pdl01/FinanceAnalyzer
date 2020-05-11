/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import financialanalyzer.objects.Company;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class AdvfnAMEXCompanyNameProvider extends AbstractCompanyNameProvider implements CompanyNameProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvfnAMEXCompanyNameProvider.class.getName());

    private static String download_url = "https://old.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=amex&render=download";
    //private static String latest_filename = AppConfig.companyDownloadDir + "/amex_latest.csv";

    public static final String IDENTIFIER = "amex";

    @Override
    public List<Company> getAllCompanies() {
        LOGGER.info("Starting getAllCompanies");
        this.downloadCSVForExchangeFromNasDaq(download_url, this.getLatestFileName());
        List<Company> companies = this.processCSVForExchangedFromNasDaq(this.getLatestFileName(), "amex");

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
        return this.appConfig.getCompanyDownloadDir() + "/amex_latest.csv";
    }

    /*
    @Override
    public List<StockHistory> getStockHistoryForCompany(String _symbol) {
        return this.getStockHistoryForCompanyForDay(_symbol,null);
    }

    @Override
    public List<StockHistory> getStockHistoryForCompanyForDay(String _symbol, Date _date) {
        return this.downloadAndProcessCSVFromNasDaq(CompanyNameProvider.EXCHANGE_AMEX,_symbol, _date);
        //return this.downloadTimeHistoryAlphavantage(CompanyProvider.EXCHANGE_AMEX,_symbol, _date);
    }
     */

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

}
