/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockpriceprediction;

import financialanalyzer.stockhistory.StockHistoryDownloadDriver;
import financialanalyzer.objects.Company;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockhistory.StockHistorySearchRepo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class StockPricePredictionServiceImpl implements StockPricePredictionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPricePredictionServiceImpl.class.getName());
    
    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;
    
    @Autowired
    private FutureStockPriceRepo futureStockPriceSearchRepo;
    
    @Override
    public FutureStockPrice getFutureStockPrice(Company _company, int _futureDays) {
        
        StockHistorySearchProperties _shsp = new StockHistorySearchProperties();
        _shsp.setStockExchange(_company.getStockExchange());
        _shsp.setStockSymbol(_company.getStockSymbol());
        List<StockHistory> shs = this.stockHistorySearchRepo.searchForStockHistory(_shsp);
        
        
        FutureStockPrice fsp = new FutureStockPrice();
        return fsp;
    }
    
}
