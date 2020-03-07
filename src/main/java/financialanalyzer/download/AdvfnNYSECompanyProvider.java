/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.download;

import financialanalyzer.objects.Company;
import financialanalyzer.objects.StockHistory;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class AdvfnNYSECompanyProvider extends AbstractCompanyProvider implements CompanyProvider {

    private static final Logger LOGGER = Logger.getLogger(AdvfnNYSECompanyProvider.class.getName());

    private static String download_url = "https://old.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
    private static String latest_filename = "/temp/nyse_latest.csv";

    @Override
    public List<Company> getAllCompanies() {
        LOGGER.info("Starting getAllCompanies");
        this.downloadCSVForExchangeFromNasDaq(download_url, latest_filename);
        List<Company> companies = this.processCSVForExchangedFromNasDaq(AdvfnNYSECompanyProvider.latest_filename, "nyse");

        LOGGER.info("Ending getAllCompanies");
        return companies;
    }

    @Override
    public List<Company> getCompaniesBeginningWithLetter(String _letter) {
        LOGGER.info("Starting getCompaniesBeginningWithLetter");
        LOGGER.info("Ending getCompaniesBeginningWithLetter");
        return null;
    }

    @Override
    public List<StockHistory> getStockHistoryForCompany(String _symbol) {
        return this.getStockHistoryForCompanyForDay(_symbol,null);
    }

    @Override
    public List<StockHistory> getStockHistoryForCompanyForDay(String _symbol, Date _date) {
        return this.downloadTimeHistoryAlphavantage(CompanyProvider.EXCHANGE_NYSE,_symbol, _date);
    }

}
