/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.stockhistory.StockHistoryDownloadTask;
import financialanalyzer.stockhistory.StockHistoryDownloadTaskReceiver;
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
 * @author phil
 */
@Component
public class StockPerformanceReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPerformanceReceiver.class.getName());

    @Autowired
    private StockPerformanceService stockPerformanceServiceImpl;

    @JmsListener(destination = ActiveMQConfig.STOCK_PERFORMANCE_QUEUE)
    public void receiveMespssage(@Payload Company _company,
            @Headers MessageHeaders headers,
            Message message, Session session) {
        LOGGER.info("Received " + _company.getStockSymbol());
        StockPerformance sp = this.stockPerformanceServiceImpl.buildStockPerformanceRecordForCompany(_company);
        if (sp != null) {
            this.stockPerformanceServiceImpl.saveStockPerformance(sp);
        }
    }

}
