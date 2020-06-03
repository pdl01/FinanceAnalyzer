/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.health;

import financialanalyzer.companynames.CompanyNameProvider;
import financialanalyzer.companynames.CompanyRepo;
import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.CompanyNewsRepo;
import financialanalyzer.companynews.CompanyNewsSearchProperties;
import financialanalyzer.companynews.CompanyNewsSearchRepo;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.lucene.spatial.prefix.tree.S2PrefixTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class HealthServiceImpl implements HealthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthServiceImpl.class.getName());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private boolean isBuilding = false;
    @Autowired
    private HealthRecordCache healthRecordCache;

    @Autowired
    private CompanyNewsRepo companyNewsSearchRepo;

    @Autowired
    private CompanyRepo companySearchRepo;

    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;

    @Override
    public HealthRecord generateHealthRecord() {
        if (this.isBuilding) {
            LOGGER.info("already building. Exiting");
            return null;
        }
        try {
            this.isBuilding = true;

            HealthRecord hr = new HealthRecord();
            Date recordDate = new Date();
            hr.setRecordDate(recordDate);
            hr.setRecordDateAsString(sdf.format(recordDate));
            //hr.setCompaniesWithoutNewsItemsInPast3Days(this.generateListOfCompaniesWithoutNewsItemsInPastXDays(3));
            //hr.setCompaniesWithoutNewsItemsInPast7Days(this.generateListOfCompaniesWithoutNewsItemsInPastXDays(7));
            hr.setCompaniesWithoutNewsItemsInPast30Days(this.generateListOfCompaniesWithoutNewsItemsInPastXDays(30));
            hr.setCompaniesWithoutStockHistoriesInPast7Days(this.generateListOfCompaniesWithoutStockHistoriesInPastXDays(7));
            this.isBuilding = false;
            //save the record
            this.healthRecordCache.clearCache();
            this.healthRecordCache.put(hr.getRecordDateAsString(), hr);
            return hr;
        } catch (Exception e) {
            this.isBuilding = false;
            LOGGER.error("Error occurred", e);
        }
        return null;
    }

    @Override
    public List<String> generateListOfCompaniesWithoutNewsItemsInPastXDays(int _numOfDays) {
        List<String> companyIdsToReturn = new ArrayList<>();
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
                        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
                        cnsp.setStockExchange(item.getStockExchange());
                        cnsp.setStockSymbol(item.getStockSymbol());
                        cnsp.setNumResults(10);
                        List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);

                        //TODOand search
                        if (cnis == null || (cnis != null && cnis.isEmpty())) {
                            companyIdsToReturn.add(item.getId());
                        }
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

        return companyIdsToReturn;
    }

    private List<String> generateSearchDates(int _numOfDays) {
        List<String> searchDates = null;
        if (_numOfDays == -1) {
            //do nothing; -1 indicates no dates to search for
        } else {
            //create a calendar based on todays date; loop, going backward until we have the number of items we need

        }
        return searchDates;
    }

    @Override
    public List<String> generateListOfCompaniesWithoutStockHistoriesInPastXDays(int _numOfDays) {
        List<String> companyIdsToReturn = new ArrayList<>();
        String[] exchangeArray = {CompanyNameProvider.EXCHANGE_AMEX, CompanyNameProvider.EXCHANGE_NASDAQ, CompanyNameProvider.EXCHANGE_NYSE};

        //TODO create list of dates to search for; need to convert search date logic to use strings;
        List<String> searchDates = this.generateSearchDates(_numOfDays);

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
                        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
                        shsp.setStockExchange(item.getStockExchange());
                        shsp.setStockSymbol(item.getStockSymbol());

                        shsp.setNumResults(10);
                        shsp.setSortField("recordDate");
                        shsp.setSortOrder("DESC");
                        if (searchDates != null) {
                            shsp.setSearchDates(searchDates);
                        }
                        List<StockHistory> shs = this.stockHistorySearchRepo.searchForStockHistory(shsp);

                        if (shs == null || (shs != null && shs.isEmpty())) {
                            companyIdsToReturn.add(item.getId());
                        }
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

        return companyIdsToReturn;
    }

    @Override
    @Scheduled(cron = "0 0 10 * * ?", zone = "UTC")
    public HealthRecord generateDailyHealthRecord() {
        //build the record
        HealthRecord hr = this.generateHealthRecord();

        return hr;
    }

    @Override
    public List<String> getAvailableHealthRecords() {
        return this.healthRecordCache.getKeys();
    }

    @Override
    public HealthRecord getHealthRecord(String _key) {
        return this.healthRecordCache.get(_key);
    }

}
