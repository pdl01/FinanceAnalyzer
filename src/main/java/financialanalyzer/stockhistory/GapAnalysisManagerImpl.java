package financialanalyzer.stockhistory;

import financialanalyzer.companynames.CompanyNameProvider;
import financialanalyzer.companynames.CompanySearchRepo;
import financialanalyzer.health.StockHistoryDateCount;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
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
            this.execute(mCal.getTime());

            mCal.add(Calendar.DATE, -1);
            LOGGER.debug("Comparing:" + SDF.format(mCal.getTime()) + ":" + SDF.format(beginDateCal.getTime()));
            if (mCal.before(beginDateCal)) {
                dateComparisonMet = true;
            }
        }
    }

    public void execute(Date _date) {
        String[] exchangeArray = {CompanyNameProvider.EXCHANGE_AMEX, CompanyNameProvider.EXCHANGE_NASDAQ, CompanyNameProvider.EXCHANGE_NYSE};
        for (String exchange : exchangeArray) {
            this.execute(exchange, _date);
        }
    }
    
    public void execute(String _dateAsString) {
        try {
            this.execute(SDF.parse(_dateAsString));
        } catch (ParseException ex) {
            LOGGER.error("parseException", ex);
        }

    }
    
    public boolean execute(Company _company, Date _date) {
        boolean isMarketDay = this.isDateAMarketDay(_company.getStockExchange(), _date);
        if (!isMarketDay) {
            return false;
        }
        if (_company.isDownloadStocks()) {

            StockHistorySearchProperties shsp = new StockHistorySearchProperties();
            shsp.setStockExchange(_company.getStockExchange());
            shsp.setSearchDate(SDF.format(_date));
            shsp.setStockSymbol(_company.getStockSymbol());
            long count = this.stockHistorySearchRepo.searchForStockHistoryCount(shsp);
            //if the stock doesn't have the data
            if (count == 0) {
                this.stockHistoryDownloadServiceImpl.queueCompanyForFetch(_company, _date, false);
                return true;
                //LOGGER.debug(company.getStockExchange() + ":" + company.getStockSymbol()+ ":" + SDF.format(_date) + ":need to build");
            }
        }
        return false;
    }

    public void execute(String _exchange, Date _date) {
        LOGGER.debug("Beginning " + _exchange + ":" + SDF.format(_date));
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
        csp.setSortField("symbol");
        csp.setSortOrder("ASC");
        //StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        //shsp.setStockExchange(_exchange);
        //shsp.setSearchDate(SDF.format(_date));
        while (hasMoreResults) {
            List<Company> companies = this.companySearchRepo.searchForCompany(csp);
            for (Company company : companies) {
                boolean submitToReprocess = this.execute(company, _date);
                if (submitToReprocess) {
                    totalCount++;
                }
            }

            if (companies != null && companies.size() == numResultsPerBatch) {
                hasMoreResults = true;
                csp.setStartResults(csp.getStartResults() + numResultsPerBatch);
            } else {
                hasMoreResults = false;
            }

        }
        LOGGER.info("Total:" + _exchange + ":" + SDF.format(_date) + ":need to build:" + totalCount);

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

    public StockHistoryDateCount query(Date _date) {
        StockHistorySearchProperties shsp = new StockHistorySearchProperties();

        shsp.setSearchDate(SDF.format(_date));

        long count = this.stockHistorySearchRepo.searchForStockHistoryCount(shsp);
        StockHistoryDateCount shdc = new StockHistoryDateCount();
        shdc.setRecordDateAsString(SDF.format(_date));
        shdc.setCompaniesWithStockHistory(count);
        return shdc;
    }
    public List<StockHistoryDateCount> query(int _days) {
        int beginDateOffset = -(_days);

        //get current date and find the date x days in the past,
        Date now = new Date();
        List<StockHistoryDateCount> counts = new ArrayList<>();
        Calendar mCal = Calendar.getInstance();
        mCal.setTime(now);
        mCal.add(Calendar.DATE, beginDateOffset);
        Calendar beginDateCal = Calendar.getInstance();
        beginDateCal.setTime(mCal.getTime());

        mCal.setTime(now);
        //for each date from now to present
        boolean dateComparisonMet = false;
        while (!dateComparisonMet) {
            
            StockHistoryDateCount shdc = this.query(mCal.getTime());
            counts.add(shdc);

            mCal.add(Calendar.DATE, -1);
            LOGGER.debug("Comparing:" + SDF.format(mCal.getTime()) + ":" + SDF.format(beginDateCal.getTime()));
            if (mCal.before(beginDateCal)) {
                dateComparisonMet = true;
            }
        }
        return counts;
    }    
    

}
