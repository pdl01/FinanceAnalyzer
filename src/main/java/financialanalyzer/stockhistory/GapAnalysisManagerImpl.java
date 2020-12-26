/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockhistory;

import financialanalyzer.companynames.CompanyNameProvider;
import financialanalyzer.companynames.CompanySearchRepo;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class GapAnalysisManagerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(GapAnalysisManagerImpl.class.getName());
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private CompanySearchRepo companySearchRepo;

    @Autowired
    StockHistoryDownloadService stockHistoryDownloadServiceImpl;

    @Autowired
    private StockHistorySearchRepo stockHistorySearchRepo;
    public void execute() {
        this.execute(60);
    }
    public void execute(int _days) {
        int beginDateOffset = -(_days);
        String[] exchangeArray = {CompanyNameProvider.EXCHANGE_AMEX, CompanyNameProvider.EXCHANGE_NASDAQ, CompanyNameProvider.EXCHANGE_NYSE};

        //get current date and find the date x days in the past,
        Date now = new Date();
        Calendar mCal = Calendar.getInstance();
        mCal.setTime(now);
        mCal.add(Calendar.DATE, beginDateOffset);
        Calendar beginDateCal = Calendar.getInstance();
        beginDateCal.setTime(mCal.getTime());
        
        
        
        mCal.setTime(now);
        //for each date from now to present
        boolean dateComparisonMet = false;
        while (!dateComparisonMet) {
            
            for (String exchange : exchangeArray) {
                this.processExchange(exchange, mCal.getTime());

            }
            mCal.add(Calendar.DATE, -1);
            LOGGER.debug("Comparing:"+SDF.format(mCal.getTime())+":"+SDF.format(beginDateCal.getTime()));
            if (mCal.before(beginDateCal)){
                dateComparisonMet = true;
            }
        }
    }

    private void processExchange(String _exchange, Date _date) {
        LOGGER.debug("Beginning "+ _exchange + ":" + SDF.format(_date));
        int totalCount = 0;
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setStockExchange(_exchange);
        int numResultsPerBatch = 50;
        boolean hasMoreResults = true;
        boolean isMarketDay = this.isDateAMarketDay(_exchange, _date);
        if (!isMarketDay) {
            return;
        }
        csp.setStartResults(0);
        csp.setNumResults(numResultsPerBatch);

        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        shsp.setStockExchange(_exchange);
        shsp.setSearchDate(SDF.format(_date));

        while (hasMoreResults) {
            List<Company> companies = this.companySearchRepo.searchForCompany(csp);
            for (Company company : companies) {
                if (company.isDownloadStocks()) {

                    shsp.setStockSymbol(company.getStockSymbol());
                    long count = this.stockHistorySearchRepo.searchForStockHistoryCount(shsp);
                    //if the stock doesn't have the data
                    if (count == 0) {
                        this.stockHistoryDownloadServiceImpl.queueCompanyForFetch(company, _date, false);
                        totalCount++;
                        //LOGGER.debug(company.getStockExchange() + ":" + company.getStockSymbol()+ ":" + SDF.format(_date) + ":need to build");
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
        LOGGER.info("Total:"+_exchange+":"+ SDF.format(_date) + ":need to build:"+totalCount);

    }

    private boolean isDateAMarketDay(String _exchange, Date _date) {
        Calendar mCal = Calendar.getInstance();
        mCal.setTime(_date);
        if (mCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || mCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return false;

        } else if (this.isDateAHoliday(_exchange, _date)) {
            return false;
        }

        return true;
    }

    private boolean isDateAHoliday(String _exchange, Date _date) {
        Calendar mCal = Calendar.getInstance();
        mCal.setTime(_date);
        if (mCal.get(Calendar.DAY_OF_MONTH) == 25 && mCal.get(Calendar.MONTH) == Calendar.DECEMBER) {
            return true;
        }
        return false;
    }

}
