package financialanalyzer.health;

import financialanalyzer.companynames.CompanyNameProvider;
import financialanalyzer.companynames.CompanyRepo;
import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.CompanyNewsRepo;
import financialanalyzer.companynews.CompanyNewsSearchProperties;
import financialanalyzer.companynews.CompanyNewsService;
import financialanalyzer.companynews.NewsItemRating;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.*;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private CompanyNewsService companyNewsServiceImpl;

    @Autowired
    private CompanyRepo companySearchRepo;

    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;

    @Autowired
    private StockHistoryDownloadService stockHistoryDowloadServiceImpl;

    @Override
    public HealthRecord generateHealthRecord(boolean _reProcessWhereAvailable) {
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
            List<String> companyIds = null;
            List<Company> companies, previous_step_companies = null;

            companies = this.generateListOfCompaniesWithoutNewsItemsInPastXDays(3, null);
            if (companies != null) {
                companyIds = new ArrayList<>();
                for (Company company : companies) {
                    if (company.isDownloadNews()) {
                        if (_reProcessWhereAvailable) {
                            this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
                        }
                        companyIds.add(company.getId());
                    }
                }
                hr.setCompaniesWithoutNewsItemsInPast3Days(companyIds);
                previous_step_companies = companies;
            }

            //filter down the companies in the above list since if they don't have 3 they may not have 7
            companies = this.generateListOfCompaniesWithoutNewsItemsInPastXDays(7, previous_step_companies);
            if (companies != null) {
                companyIds = new ArrayList<>();
                for (Company company : companies) {
                    if (company.isDownloadNews()) {
                        if (_reProcessWhereAvailable) {
                            this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
                        }
                        companyIds.add(company.getId());
                    }
                }
                previous_step_companies = companies;
                hr.setCompaniesWithoutNewsItemsInPast7Days(companyIds);
            }

            //filter down the companies in the above list since if they don't have 7 they may not have 30
            companies = this.generateListOfCompaniesWithoutNewsItemsInPastXDays(30, previous_step_companies);
            if (companies != null) {
                companyIds = new ArrayList<>();
                for (Company company : companies) {
                    if (company.isDownloadNews()) {
                        if (_reProcessWhereAvailable) {
                            this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
                        }
                        companyIds.add(company.getId());
                    }
                }
                hr.setCompaniesWithoutNewsItemsInPast30Days(companyIds);
            }

            companies = this.generateListOfCompaniesWithoutStockHistoriesInPastXDays(3, null);
            if (companies != null) {
                companyIds = new ArrayList<>();
                for (Company company : companies) {
                    if (company.isDownloadStocks()) {
                        if (_reProcessWhereAvailable) {
                            this.stockHistoryDowloadServiceImpl.queueCompanyForFetch(company, new Date(), true);
                        }
                        companyIds.add(company.getId());
                    }
                }
                hr.setCompaniesWithoutStockHistoriesInPast3Days(companyIds);
                previous_step_companies = companies;
            }

            companies = this.generateListOfCompaniesWithoutStockHistoriesInPastXDays(7, previous_step_companies);
            if (companies != null) {
                companyIds = new ArrayList<>();
                for (Company company : companies) {
                    if (_reProcessWhereAvailable) {
                        this.stockHistoryDowloadServiceImpl.queueCompanyForFetch(company, new Date(), true);
                    }
                    companyIds.add(company.getId());
                }
                hr.setCompaniesWithoutStockHistoriesInPast7Days(companyIds);
            }

            long totalCount = 0;
            CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedSystemRating(NewsItemRating.POSITIVE);
            cnsp.addIncludedSystemRating(NewsItemRating.NEGATIVE);
            cnsp.addIncludedSystemRating(NewsItemRating.UNRELATED);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfSystemAnalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedSystemRating(NewsItemRating.NONE);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfSystemUnanalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedUserRating(NewsItemRating.POSITIVE);
            cnsp.addIncludedUserRating(NewsItemRating.NEGATIVE);
            cnsp.addIncludedUserRating(NewsItemRating.UNRELATED);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfUserAnalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedSystemRating(NewsItemRating.POSITIVE);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfPositiveSystemAnalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedSystemRating(NewsItemRating.NEGATIVE);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfNegativeSystemAnalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedUserRating(NewsItemRating.NEGATIVE);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfNegativeUserAnalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedUserRating(NewsItemRating.POSITIVE);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfPositiveUserAnalyzedNewsItems(totalCount);

            cnsp = new CompanyNewsSearchProperties();
            cnsp.addIncludedUserRating(NewsItemRating.NONE);
            totalCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
            hr.setTotalNumberOfUserUnanalyzedNewsItems(totalCount);

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
    public List<Company> generateListOfCompaniesWithoutNewsItemsInPastXDays(int _numOfDays, List<Company> _companies) {
        List<Company> companiesToReturn = new ArrayList<>();
        String[] exchangeArray = {CompanyNameProvider.EXCHANGE_AMEX, CompanyNameProvider.EXCHANGE_NASDAQ, CompanyNameProvider.EXCHANGE_NYSE};

        List<String> searchDates = this.generateSearchDates(_numOfDays);

        if (_companies != null) {
            for (Company item : _companies) {
                CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
                cnsp.setStockExchange(item.getStockExchange());
                cnsp.setStockSymbol(item.getStockSymbol());
                cnsp.setNumResults(10);
                //List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
                if (searchDates != null) {
                    cnsp.setSearchDates(searchDates);
                }
                long companyNewsItemCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
                if (companyNewsItemCount < 1) {
                    companiesToReturn.add(item);
                }
                //TODOand search
                //if (cnis == null || (cnis != null && cnis.isEmpty())) {
                //    companiesToReturn.add(item);
                //}
            }
        } else {

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
                            //List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
                            if (searchDates != null) {
                                cnsp.setSearchDates(searchDates);
                            }
                            long companyNewsItemCount = this.companyNewsSearchRepo.searchForCompanyNewsCount(cnsp);
                            if (companyNewsItemCount < 1) {
                                companiesToReturn.add(item);
                            }
                            //TODOand search
                            //if (cnis == null || (cnis != null && cnis.isEmpty())) {
                            //    companiesToReturn.add(item);
                            //}
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
        }

        return companiesToReturn;
    }

    private List<String> generateIdsFromCompanyList(List<Company> _companies) {
        ArrayList<String> ids = new ArrayList<>();
        return ids;
    }

    private List<String> generateSearchDates(int _numOfDays) {
        List<String> searchDates = null;
        searchDates = new ArrayList<>();
        if (_numOfDays == -1) {
            //do nothing; -1 indicates no dates to search for
        } else {
            //create a calendar based on todays date; loop, going backward until we have the number of items we need
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            for (int i = 0; i < _numOfDays; i++) {
                Date theDate = cal.getTime();
                searchDates.add(sdf.format(theDate));
                cal.add(Calendar.DATE, -1);
            }
        }
        return searchDates;
    }

    @Override
    public List<Company> generateListOfCompaniesWithoutStockHistoriesInPastXDays(int _numOfDays, List<Company> _companies) {
        List<Company> companiesToReturn = new ArrayList<>();

        //TODO create list of dates to search for; need to convert search date logic to use strings;
        List<String> searchDates = this.generateSearchDates(_numOfDays);

        if (_companies != null) {
            for (Company item : _companies) {
                StockHistorySearchProperties shsp = new StockHistorySearchProperties();
                shsp.setStockExchange(item.getStockExchange());
                shsp.setStockSymbol(item.getStockSymbol());

                //shsp.setNumResults(10);
                //shsp.setSortField("recordDate");
                //shsp.setSortOrder("DESC");
                if (searchDates != null) {
                    shsp.setSearchDates(searchDates);
                }
                long totalCount = this.stockHistorySearchRepo.searchForStockHistoryCount(shsp);

                if (totalCount < 1) {
                    companiesToReturn.add(item);
                }
            }
        } else {
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
                            StockHistorySearchProperties shsp = new StockHistorySearchProperties();
                            shsp.setStockExchange(item.getStockExchange());
                            shsp.setStockSymbol(item.getStockSymbol());

                            //shsp.setNumResults(10);
                            //shsp.setSortField("recordDate");
                            //shsp.setSortOrder("DESC");
                            if (searchDates != null) {
                                shsp.setSearchDates(searchDates);
                            }
                            long totalCount = this.stockHistorySearchRepo.searchForStockHistoryCount(shsp);

                            if (totalCount < 1) {
                                companiesToReturn.add(item);
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
        }
        return companiesToReturn;
    }

    @Override
    @Scheduled(cron = "0 0 10 * * ?", zone = "UTC")
    public HealthRecord generateDailyHealthRecord() {
        //build the record
        HealthRecord hr = this.generateHealthRecord(true);

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
