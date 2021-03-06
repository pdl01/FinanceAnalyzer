/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockhistory;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import java.util.List;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class StockHistoryDownloadTaskReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockHistoryDownloadTaskReceiver.class.getName());

    @Autowired
    StockHistoryDownloadService stockHistoryDownloadServiceImpl;
    @Autowired
    private StockHistorySearchRepo stockHistorySearchRepo;

    @JmsListener(destination = ActiveMQConfig.STOCK_HISTORY_DOWNLOAD_QUEUE)
    public void receiveMessage(@Payload StockHistoryDownloadTask _stockHistoryDownloadTask,
            @Headers MessageHeaders headers,
            Message message, Session session) {
        LOGGER.info("Received " + _stockHistoryDownloadTask.getSymbol());

        List<StockHistory> shs = null;
        Company company = new Company();
        //TODO get this from the repo instead of the queue
        company.setStockExchange(_stockHistoryDownloadTask.getExchange());
        company.setStockSymbol(_stockHistoryDownloadTask.getSymbol());
        company.setSectors(_stockHistoryDownloadTask.getSectors());
        company.setIndustries(_stockHistoryDownloadTask.getIndustries());
        if (_stockHistoryDownloadTask.isDownloadAllAvailalble()) {
            shs = this.stockHistoryDownloadServiceImpl.fetchDataForCompany(company);
        } else {
            shs = this.stockHistoryDownloadServiceImpl.fetchDataForCompany(company, _stockHistoryDownloadTask.getRetrieveDate());
        }
        /*
        if (shs != null) {
            LOGGER.info("Submitting stock history data for :" + company.getStockSymbol());
            for (StockHistory shs_item : shs) {
                this.stockHistorySearchRepo.submit(shs_item);
            }

            LOGGER.info("Completed submitting stock history data for :" + company.getStockSymbol());

        }
        */
        try {
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            LOGGER.info("Interruption", ex);
        }  
        LOGGER.info("Finished Processing " + _stockHistoryDownloadTask.getSymbol());

    }

}
