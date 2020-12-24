/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockhistory;

import financialanalyzer.elasticsearch.ElasticSearchManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteRequest;
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
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class StockHistorySearchRepo extends ElasticSearchManager implements StockHistoryRepo {

    private static final Logger logger = LoggerFactory.getLogger(StockHistorySearchRepo.class.getName());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public StockHistory submit(StockHistory _item) {
        if (_item == null) {
            return null;
        }
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return null;
        }

        IndexRequest indexRequest = new IndexRequest("stockhistories", "stockhistory", this.getKey(_item))
                .source("id", this.getKey(_item),
                        "recordDate", sdf.format(_item.getRecordDate()),
                        "symbol", _item.getSymbol(),
                        "exchange", _item.getExchange(),
                        "open", _item.getOpen(),
                        "close", _item.getClose(),
                        "percent_gain", _item.getPercent_gain(),
                        "actual_gain", _item.getActual_gain(),
                        "volume", _item.getVolume(),
                        "high", _item.getHigh(),
                        "low", _item.getLow(),
                        "industry", _item.getIndustries(),
                        "sector", _item.getSectors(),
                        "enhancementVersion", _item.getEnhancementVersion()
                );
        int retryCounter = 0;
        boolean indexedSuccessfully = false;
        while (!indexedSuccessfully && retryCounter < 3) {

            try {
                logger.info("Index Attempt:" + retryCounter + ":" + this.getKey(_item));
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

    private String getKey(StockHistory _item) {
        return _item.getExchange() + "-" + _item.getSymbol() + "-" + sdf.format(_item.getRecordDate());
    }

    @Override
    public boolean delete(StockHistory _item) {
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return false;
        }

        DeleteRequest request = new DeleteRequest("stockhistories", "stockhistory", this.getKey(_item));

        try {
            RequestOptions options = null;
            client.delete(request, options);
            //CreateIndexRequest request = new CreateIndexRequest("users");
            //client.
            //CreateIndexResponse  createIndexResponse = client.indices().indices().create(request);
        } catch (IOException ex) {
            logger.error("Error when deleting stock history " + ex.getMessage());

        } catch (Exception ex) {
            logger.error("Error when deleting stock history " + ex.getMessage());

        }

        this.closeClient(client);
        return true;
    }

    @Override
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
            boolQuery.must(QueryBuilders.termQuery("exchange", _shsp.getStockExchange()));

        }
        if (_shsp.getStockSymbol() != null) {
            boolQuery.must(QueryBuilders.termQuery("symbol", _shsp.getStockSymbol()));

        }

        if (_shsp.getSearchDates() != null) {
            BoolQueryBuilder dateQuery = QueryBuilders.boolQuery();
            for (String dateForQuery : _shsp.getSearchDates()) {
                dateQuery.should(QueryBuilders.termQuery("recordDate", dateForQuery));
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
                if (hit.getType().equalsIgnoreCase("stockhistory")) {
                    String sourceAsString = hit.getSourceAsString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    StockHistory sh = this.buildStockHistoryFromSourceMap(sourceAsMap);

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

    private StockHistory buildStockHistoryFromSourceMap(Map<String, Object> _sourceAsMap) {
        //2020-03-09T04:00:00.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String id = (String) _sourceAsMap.get("id");

        String recordDate = ((String) _sourceAsMap.get("recordDate")).substring(0, 10);

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
        List<String> sectors = (List<String>) _sourceAsMap.get("sector");
        List<String> industries = (List<String>) _sourceAsMap.get("industry");
        String enhancementVersion = (String) _sourceAsMap.get("enhancementVersion");

        StockHistory sh = new StockHistory();
        sh.setRecordDateAsString(recordDate);
        try {
            sh.setRecordDate(sdf.parse(recordDate));
        } catch (Exception e) {
            logger.error("Cannot convert recordDate from search to java date");
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

        sh.setSectors(sectors);
        sh.setIndustries(industries);
        sh.setEnhancementVersion(enhancementVersion);
        return sh;
    }

    @Override
    public long searchForStockHistoryCount(StockHistorySearchProperties _shsp) {
        List<StockHistory> shs = new ArrayList<>();
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return -1;
        }

        SearchRequest searchRequest = new SearchRequest("stockhistories");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        QueryBuilder matchQueryBuilder = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (_shsp.getStockExchange() != null) {
            boolQuery.must(QueryBuilders.termQuery("exchange", _shsp.getStockExchange()));

        }
        if (_shsp.getStockSymbol() != null) {
            boolQuery.must(QueryBuilders.termQuery("symbol", _shsp.getStockSymbol()));

        }

        if (_shsp.getSearchDates() != null) {
            BoolQueryBuilder dateQuery = QueryBuilders.boolQuery();
            for (String dateForQuery : _shsp.getSearchDates()) {
                dateQuery.should(QueryBuilders.termQuery("recordDate", dateForQuery));
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
        long numOfHits = -1;
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            //SearchHit[] searchHits = hits.getHits();
            numOfHits =  hits.getTotalHits();

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        this.closeClient(client);

        return numOfHits;
    }
}
