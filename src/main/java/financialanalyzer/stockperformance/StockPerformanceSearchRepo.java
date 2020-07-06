/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import financialanalyzer.elasticsearch.ElasticSearchManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author phil
 */
@Service
public class StockPerformanceSearchRepo extends ElasticSearchManager implements StockPerformanceRepo {
    private static final Logger logger = LoggerFactory.getLogger(StockPerformanceSearchRepo.class.getName());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public StockPerformance submit(StockPerformance _item) {
        if (_item == null) {
            return null;
        }
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return null;
        }

        IndexRequest indexRequest = new IndexRequest("companystockperformance", "stockperformancerecord", _item.getId())
                .source("id", _item.getId(),
                        "recordDate", sdf.format(_item.getRecordDate()),
                        "symbol", _item.getSymbol(),
                        "exchange", _item.getExchange(),
                        "threedayperf", _item.getThreedayperf(),
                        "sevendayperf", _item.getSevendayperf(),
                        "thirtydayperf", _item.getThirtydayperf(),
                        "industry", _item.getIndustries(),
                        "sector", _item.getSectors()
                );
        int retryCounter = 0;
        boolean indexedSuccessfully = false;
        while (!indexedSuccessfully && retryCounter < 3) {

            try {
                logger.info("Index Attempt:" + retryCounter + ":" + _item.getId());
                IndexResponse indexResponse = client.index(indexRequest);
                //logger.info(indexResponse.getIndex());
                //logger.info(indexResponse.getResult().name());
                //TODO: inspect index response instead of just absence of exception for retry
                indexedSuccessfully = true;
            } catch (IOException ex) {
                //ex.printStackTrace();
                logger.error(ex.getMessage());
            }
            retryCounter++;
        }
        this.closeClient(client);
        return _item;

    }

    @Override
    public boolean delete(StockPerformance _item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockPerformance> searchForStockPerformance(StockPerformanceSearchProperties _shsp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long searchForStockPerformanceCount(StockPerformanceSearchProperties _shsp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
