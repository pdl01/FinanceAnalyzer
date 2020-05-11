/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.companynames.CompanySearchRepo;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import financialanalyzer.companynames.CompanyNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsDownloadDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyNewsDownloadDriver.class.getName());


    @Autowired
    private CompanySearchRepo companySearchRepo;
    
    @Autowired
    private CompanyNewsService companyNewsServiceImpl;
    

    @Scheduled(cron = "0 0 22 * * ?")
    public void fetchDaily() {
        LOGGER.info("Starting fetchDaily");

        Date todaysDate = new Date();
        this.fetchLatestData(todaysDate);
        LOGGER.info("Completed fetchDaily");
    }

    public void fetchLatestData(Date _date) {
        String[] exchangeArray = {CompanyNameProvider.EXCHANGE_AMEX, CompanyNameProvider.EXCHANGE_NASDAQ, CompanyNameProvider.EXCHANGE_NYSE};

        for (String exchange : exchangeArray) {
            CompanySearchProperties csp = new CompanySearchProperties();
            csp.setStockExchange(exchange);
            int numResultsPerBatch = 200;
            boolean hasMoreResults = true;
            csp.setStartResults(0);
            csp.setNumResults(numResultsPerBatch);

            while (hasMoreResults) {
                List<Company> companies = this.companySearchRepo.searchForCompany(csp);
                if (companies != null) {
                    LOGGER.info("Processing :" + companies.size() + " companies");
                } else {
                    LOGGER.info("No companies returned");
                }
                if (companies != null) {
                    for (Company item : companies) {
                        LOGGER.info("Submitting:" + item.getStockExchange() + item.getName() + ":" + item.getStockSymbol());
                        this.companyNewsServiceImpl.submitCompanyToDownloadQueue(item);
                    }
                }
                if (companies != null && companies.size() == numResultsPerBatch) {
                    hasMoreResults = true;
                    csp.setStartResults(csp.getStartResults() + numResultsPerBatch);
                } else {
                    hasMoreResults = false;
                }
            }
         
        }
        LOGGER.info("Ending fetchLatestData");
    }
}

