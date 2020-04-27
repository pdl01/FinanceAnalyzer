/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockhistory;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.systemactivity.SystemActivityManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import financialanalyzer.companynames.CompanyNameProvider;

/**
 *
 * @author pldor
 */
@Component
public class StockHistoryDowloadServiceImpl implements StockHistoryDownloadService {
    private static final Logger LOGGER = Logger.getLogger(StockHistoryDowloadServiceImpl.class.getName());

    @Autowired
    private StockHistorySearchRepo stockHistorySearchRepoImpl;

    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private StockHistoryProviderRegistry stockHistoryProviderRegistry;
    

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
            LOGGER.severe("Unable to locate a StockHistory Provider");
            return null;
        }
        
        if (_date != null) {
            shs = shProvider.getStockHistoryForCompany(company.getStockExchange(),company.getStockSymbol());

        } else {
            shs = shProvider.getStockHistoryForCompanyForDay(company.getStockExchange(),company.getStockSymbol(), _date);

        }
        if (shs != null) {
            for (StockHistory item : shs) {
                this.stockHistorySearchRepoImpl.submit(item);
            }
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
        LOGGER.info("queueing _item:"+_item.getSymbol());
        this.jmsTemplate.convertAndSend(ActiveMQConfig.STOCK_HISTORY_DOWNLOAD_QUEUE, _item);
    }

}
