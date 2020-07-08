/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import financialanalyzer.elasticsearch.ElasticSearchManager;
import financialanalyzer.stockhistory.StockHistory;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
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

        _item.setId(_item.getExchange()+"-"+_item.getSymbol()+"-"+sdf.format(_item.getRecordDate()));
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
        List<StockPerformance> shs = new ArrayList<>();
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return shs;
        }

        SearchRequest searchRequest = new SearchRequest("companystockperformance");
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

        if (_shsp.getSearchDates() != null) {
            BoolQueryBuilder dateQuery = QueryBuilders.boolQuery();
            for (String dateForQuery : _shsp.getSearchDates()) {
                dateQuery.should(QueryBuilders.matchQuery("recordDate", dateForQuery));
            }
            boolQuery.must(dateQuery);

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
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                //build some artificial items that will house basic info about the artifact, without hitting the main db again. (id,title)
                if (hit.getType().equalsIgnoreCase("stockperformancerecord")) {
                    String sourceAsString = hit.getSourceAsString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    StockPerformance sh = this.buildStockPerformanceFromSourceMap(sourceAsMap);

                    shs.add(sh);
                }
                // do something with the SearchHit
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        this.closeClient(client);

        return shs;

    }

    @Override
    public long searchForStockPerformanceCount(StockPerformanceSearchProperties _shsp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private StockPerformance buildStockPerformanceFromSourceMap(Map<String, Object> _sourceAsMap) {
        //2020-03-09T04:00:00.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String id = (String) _sourceAsMap.get("id");

        String recordDate = ((String) _sourceAsMap.get("recordDate")).substring(0, 10);

        String symbol = (String) _sourceAsMap.get("symbol");
        String exchange = (String) _sourceAsMap.get("exchange");
        //even though mapping is float, sourcemap is being returned as double; need to make unsafe cast, but should be fine
        //https://github.com/elastic/elasticsearch/issues/25792
        float threedayperf = ((Double) _sourceAsMap.get("threedayperf")).floatValue();
        float sevendayperf = ((Double) _sourceAsMap.get("sevendayperf")).floatValue();
        float thirtydayperf = ((Double) _sourceAsMap.get("thirtydayperf")).floatValue();
        List<String> sectors = (List<String>) _sourceAsMap.get("sector");
        List<String> industries = (List<String>) _sourceAsMap.get("industry");

        StockPerformance sh = new StockPerformance();
        sh.setId(id);
        sh.setRecordDateAsString(recordDate);
        try {
            sh.setRecordDate(sdf.parse(recordDate));
        } catch (Exception e) {
            logger.error("Cannot convert recordDate from search to java date");
        }
        sh.setThirtydayperf(thirtydayperf);
        sh.setThreedayperf(threedayperf);
        sh.setSevendayperf(sevendayperf);
        

        sh.setExchange(exchange);
        sh.setSymbol(symbol);

        sh.setSectors(sectors);
        sh.setIndustries(industries);
        
        return sh;
    }
    
    
}
