/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockhistory;

import financialanalyzer.elasticsearch.ElasticSearchManager;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class StockHistoryReportSearchRepo extends ElasticSearchManager {

    private static final Logger logger = Logger.getLogger(StockHistoryReportSearchRepo.class.getName());
    public static final String STOCK_HISTORY_INDEX = "stockhistories";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public void searchForReport (SearchRequest _sr){
        
    }
    
    public List<StockHistory> searchForStockHistory(StockHistorySearchProperties _shsp) {
        List<StockHistory> shs = new ArrayList<>();
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return shs;
        }

        SearchRequest searchRequest = new SearchRequest("stockhistories");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        QueryBuilder matchQueryBuilder = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        if (_shsp.getStockExchange() != null) {
            boolQuery.must(QueryBuilders.matchQuery("exchange", _shsp.getStockExchange()));

        }
        if (_shsp.getStockSymbol() != null) {
            boolQuery.must(QueryBuilders.matchQuery("symbol", _shsp.getStockSymbol()));

        }
        if (_shsp.getSearchDate() != null) {
            try {
                boolQuery.must(QueryBuilders.matchQuery("recordDate", sdf.parse(_shsp.getSearchDate())));
            } catch (ParseException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

        }

        //.fuzziness(Fuzziness.AUTO);
        searchSourceBuilder.query(boolQuery).from(_shsp.getStartResults()).size(_shsp.getNumResults());

        if (_shsp.getSortField() != null) {
            //TODO sort based on dimension type
            if ("ASC".equalsIgnoreCase(_shsp.getSortOrder())) {
                searchSourceBuilder.sort(_shsp.getSortField(), SortOrder.ASC);
            } else {
                searchSourceBuilder.sort(_shsp.getSortField(), SortOrder.DESC);
            }
        }
        
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                //build some artificial items that will house basic info about the artifact, without hitting the main db again. (id,title)
                if (hit.getType().equalsIgnoreCase("stockhistory")) {
                    String sourceAsString = hit.getSourceAsString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    StockHistory sh = this.buildStockHistoryFromSourceMap(sourceAsMap);

                    shs.add(sh);
                }
                // do something with the SearchHit
            }
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }

        this.closeClient(client);

        return shs;

    }

    private StockHistory buildStockHistoryFromSourceMap(Map<String, Object> _sourceAsMap) {
        //2020-03-09T04:00:00.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String id = (String) _sourceAsMap.get("id");

        String recordDate = ((String) _sourceAsMap.get("recordDate")).substring(0,10);
        
        String symbol = (String) _sourceAsMap.get("symbol");
        String exchange = (String) _sourceAsMap.get("exchange");
        //even though mapping is float, sourcemap is being returned as double; need to make unsafe cast, but should be fine
        //https://github.com/elastic/elasticsearch/issues/25792
        float openValue = ((Double) _sourceAsMap.get("open")).floatValue();
        float closeValue = ((Double) _sourceAsMap.get("close")).floatValue();
        float percent_gain = ((Double) _sourceAsMap.get("percent_gain")).floatValue();
        float actual_gain = ((Double) _sourceAsMap.get("actual_gain")).floatValue();
        float high = ((Double) _sourceAsMap.get("high")).floatValue();
        float low = ((Double) _sourceAsMap.get("low")).floatValue();
        int volume = (int) _sourceAsMap.get("volume");
        
        
        
        StockHistory sh = new StockHistory();
        sh.setRecordDateAsString(recordDate);
        try {
            sh.setRecordDate(sdf.parse(recordDate));    
        } catch (Exception e) {
            logger.severe("Cannot convert recordDate from search to java date");
        }
        
        sh.setActual_gain(actual_gain);
        sh.setVolume(volume);
        sh.setPercent_gain(percent_gain);
        sh.setOpen(openValue);
        sh.setClose(closeValue);
        sh.setHigh(high);
        sh.setLow(low);
        
        sh.setExchange(exchange);
        sh.setSymbol(symbol);
        
        
        return sh;
    }
}
