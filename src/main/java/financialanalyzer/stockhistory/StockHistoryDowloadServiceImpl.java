package financialanalyzer.stockhistory;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.stockperformance.StockPerformanceService;
import financialanalyzer.systemactivity.SystemActivityManager;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pldor
 */
@Component
public class StockHistoryDowloadServiceImpl implements StockHistoryDownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockHistoryDowloadServiceImpl.class.getName());

    @Autowired
    private StockHistorySearchRepo stockHistorySearchRepoImpl;

    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private StockHistoryProviderRegistry stockHistoryProviderRegistry;

    @Autowired
    private StockPerformanceService stockPerformanceServiceImpl;

    private String version = "1.0.0";

    @Override
    public List<StockHistory> fetchDataForCompany(Company company) {
        return this.fetchDataForCompany(company, null);
    }

    @Override
    public List<StockHistory> fetchDataForCompany(Company company, Date _date) {
        if (company == null) {
            return null;
        }
        List<StockHistory> shs = null;

        StockHistoryProvider shProvider = this.stockHistoryProviderRegistry.getPreferredProvider();
        if (shProvider == null) {
            LOGGER.error("Unable to locate a StockHistory Provider");
            return null;
        }

        if (_date != null) {
            shs = shProvider.getStockHistoryForCompany(company.getStockExchange(), company.getStockSymbol());

        } else {
            shs = shProvider.getStockHistoryForCompanyForDay(company.getStockExchange(), company.getStockSymbol(), _date);

        }
        if (shs != null) {
            for (StockHistory item : shs) {
                item.setEnhancementVersion(version);
                item.setIndustries(company.getIndustries());
                item.setSectors(company.getSectors());
                this.stockHistorySearchRepoImpl.submit(item);

            }
            //submit to build performance report
            this.stockPerformanceServiceImpl.queueCompanyForBuild(company);

            this.systemActivityManagerImpl.saveSystemActivity(company.getStockSymbol(), company.getStockExchange(), SystemActivityManager.ACTIVITY_TYPE_STOCK_HISTORY_DOWNLOAD, "Updated items:" + shs.size());
            /*
            shs.forEach(item -> {
                this.stockHistorySearchRepoImpl.submit(item);

            });
             */
        }
        return shs;
    }

    @Override
    public void queueCompanyForFetch(Company item, Date _date, boolean retrieveAll) {
        StockHistoryDownloadTask shdt = new StockHistoryDownloadTask();
        shdt.setSymbol(item.getStockSymbol());
        shdt.setExchange(item.getStockExchange());
        shdt.setIndustries(item.getIndustries());
        shdt.setSectors(item.getSectors());
        if (_date == null) {
            shdt.setDownloadAllAvailalble(true);
            //shs = this.stockHistoryDownloadServiceImpl.fetchDataForCompany(item);
            //shs = this.advfnNasDaqCompanyProvider.getStockHistoryForCompany(item.getStockSymbol());
        } else {
            shdt.setDownloadAllAvailalble(false);
            shdt.setRetrieveDate(_date);
            //shs = this.stockHistoryDownloadServiceImpl.fetchDataForCompany(item, _date);
        }
        this.queueStockHistoryDownloadTask(shdt);
    }

    @Override
    public void queueStockHistoryDownloadTask(StockHistoryDownloadTask _item) {
        LOGGER.info("queueing _item:" + _item.getSymbol());
        this.jmsTemplate.convertAndSend(ActiveMQConfig.STOCK_HISTORY_DOWNLOAD_QUEUE, _item);
    }

    @Override
    public void runReconcileForCompany(Company _company) {
        //find earliest date in the system
        //use 60 days ago or that date which ever is earlier
        //starting with the basedate, generate a list of dates
        //if the date should have a record, and there is no record 
        //download and build a record
       
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void runReconcileForSystem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
