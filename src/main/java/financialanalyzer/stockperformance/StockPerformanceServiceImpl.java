/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistoryDowloadServiceImpl;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import financialanalyzer.stockhistory.StockHistorySearchRepo;
import financialanalyzer.systemactivity.SystemActivityManager;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author phil
 */
@Service
public class StockPerformanceServiceImpl implements StockPerformanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPerformanceServiceImpl.class.getName());

    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private StockHistorySearchRepo stockHistorySearchRepoImpl;

    @Autowired
    private StockPerformanceSearchRepo stockPerformanceSearchRepoImpl;

    @Override
    public void queueCompanyForBuild(Company company) {
        this.jmsTemplate.convertAndSend(ActiveMQConfig.STOCK_PERFORMANCE_QUEUE, company);
    }

    @Override
    public StockPerformance buildStockPerformanceRecordForCompany(Company _company) {
        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        shsp.setStockExchange(_company.getStockExchange());
        shsp.setStockSymbol(_company.getStockSymbol());
        shsp.setSortField("recordDate");
        shsp.setSortOrder("DESC");
        shsp.setNumResults(30);
        List<StockHistory> items = this.stockHistorySearchRepoImpl.searchForStockHistory(shsp);

        //get last 30 days sorted descending
        //calculate % gain from open of first to close of last
        int counter = 0;
        float closingValue = items.get(0).getClose();
        StockPerformance sp = this.createStockPerformanceFromCompany(_company);
        sp.setCurrent(closingValue);
        //TODO: validation check
        //check if the first item in the list is from the day before
        //if it is continue;
        //if it is not, queue the company and start again.
        //StockHistory sh = items.get(0);
        for (StockHistory item : items) {

            if (counter == 2) {
                sp.setThreedayopen(item.getOpen());
                sp.setThreedayperf((closingValue - item.getOpen()) / item.getOpen());
            } else if (counter == 6) {
                sp.setSevendayopen(item.getOpen());
                sp.setSevendayperf((closingValue - item.getOpen()) / item.getOpen());
            } else if (counter == 29) {
                sp.setThirtydayopen(item.getOpen());
                sp.setThirtydayperf((closingValue - item.getOpen()) / item.getOpen());
            } else if (counter == 0) {
                //set the stock performance date record to the first item in the array, since this is what its based on
                
                sp.setRecordDate(item.getRecordDate());
            }
            counter++;
        }
        LOGGER.debug(sp.getExchange() + ":" + sp.getSymbol() + ":3day:" + sp.getThreedayperf() + ":7day:" + sp.getSevendayperf() + ":30day:" + sp.getThirtydayperf());
        //persist to the repo
        return sp;
    }

    private StockPerformance createStockPerformanceFromCompany(Company _company) {
        StockPerformance sp = new StockPerformance();
        sp.setExchange(_company.getStockExchange());
        sp.setSymbol(_company.getStockSymbol());
        sp.setSectors(_company.getSectors());
        sp.setIndustries(_company.getIndustries());
        //sp.setRecordDate(new Date());
        return sp;
    }

    @Override
    public void saveStockPerformance(StockPerformance sp) {
        this.stockPerformanceSearchRepoImpl.submit(sp);
    }
}
