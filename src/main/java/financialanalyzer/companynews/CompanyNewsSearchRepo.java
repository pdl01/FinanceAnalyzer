/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.elasticsearch.ElasticSearchManager;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import financialanalyzer.stockhistory.StockHistoryReportSearchRepo;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsSearchRepo extends ElasticSearchManager implements CompanyNewsRepo {

    private static final Logger logger = Logger.getLogger(CompanyNewsSearchRepo.class.getName());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public CompanyNewsItem submit(CompanyNewsItem _item) {
        logger.info("processing submit");
        if (_item == null) {
            return null;
        }
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return null;
        }

        IndexRequest indexRequest = new IndexRequest("companynews", "companynewsitem", _item.getId())
                .source("id", _item.getId(),
                        "recordDate", sdf.format(_item.getRecordDate()),
                        "publishedDate", sdf.format(_item.getPublishedDate()),
                        "symbol", _item.getSymbol(),
                        "exchange", _item.getExchange(),
                        "subject", _item.getSubject(),
                        "url", _item.getUrl(),
                        "body", _item.getBody(),
                        "sentiment", _item.getSentiment()
                );

        int retryCounter = 0;
        boolean indexedSuccessfully = false;
        while (!indexedSuccessfully && retryCounter < 3) {

            try {
                IndexResponse indexResponse = client.index(indexRequest);
                //logger.info(indexResponse.getIndex());
                //logger.info(indexResponse.getResult().name());
                indexedSuccessfully = true;
            } catch (IOException ex) {
                //ex.printStackTrace();
                logger.severe(ex.getMessage());
                indexedSuccessfully = false;
            }
        }
        this.closeClient(client);
        return _item;
    }

    @Override
    public boolean delete(CompanyNewsItem _item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CompanyNewsItem> searchForCompanyNews(CompanyNewsSearchProperties _sp) {
        logger.info("Beginning Search");
        List<CompanyNewsItem> cnis = new ArrayList<>();
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            logger.severe("Client is null");
            return cnis;
        }

        SearchRequest searchRequest = new SearchRequest("companynews");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        QueryBuilder matchQueryBuilder = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (_sp.getStockExchange() != null) {
            boolQuery.must(QueryBuilders.matchQuery("exchange", _sp.getStockExchange()));

        }
        if (_sp.getStockSymbol() != null) {
            boolQuery.must(QueryBuilders.matchQuery("symbol", _sp.getStockSymbol()));
        }
        if (_sp.getCompanyNewsItemId() != null) {
            boolQuery.must(QueryBuilders.matchQuery("_id", _sp.getCompanyNewsItemId()));
        }
        if (_sp.getSearchDate() != null) {
            //try {
            boolQuery.must(QueryBuilders.matchQuery("recordDate", _sp.getSearchDate()));
            //} catch (ParseException ex) {
            //    logger.log(Level.SEVERE, null, ex);
            //}

        }

        //.fuzziness(Fuzziness.AUTO);
        searchSourceBuilder.query(boolQuery).from(_sp.getStartResults()).size(_sp.getNumResults());

        if (_sp.getSortField() != null) {
            //TODO sort based on dimension type
            if ("ASC".equalsIgnoreCase(_sp.getSortOrder())) {
                searchSourceBuilder.sort(_sp.getSortField(), SortOrder.ASC);
            } else {
                searchSourceBuilder.sort(_sp.getSortField(), SortOrder.DESC);
            }
        }

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                //build some artificial items that will house basic info about the artifact, without hitting the main db again. (id,title)
                if (hit.getType().equalsIgnoreCase("companynewsitem")) {
                    String sourceAsString = hit.getSourceAsString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    CompanyNewsItem cni = this.buildCompanyNewsItemFromSourceMap(sourceAsMap);

                    cnis.add(cni);
                }
                // do something with the SearchHit
            }
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }

        this.closeClient(client);
        logger.info("Returning frm search");
        return cnis;

    }

    private CompanyNewsItem buildCompanyNewsItemFromSourceMap(Map<String, Object> _sourceAsMap) {
        //2020-03-09T04:00:00.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String id = (String) _sourceAsMap.get("id");

        String recordDate = ((String) _sourceAsMap.get("recordDate")).substring(0, 10);
        String publishedDate = ((String) _sourceAsMap.get("publishedDate")).substring(0, 10);

        String symbol = (String) _sourceAsMap.get("symbol");
        String exchange = (String) _sourceAsMap.get("exchange");
        //even though mapping is float, sourcemap is being returned as double; need to make unsafe cast, but should be fine
        //https://github.com/elastic/elasticsearch/issues/25792
        String subject = (String) _sourceAsMap.get("subject");
        String body = (String) _sourceAsMap.get("body");
        String url = (String) _sourceAsMap.get("url");
        String sentiment = (String) _sourceAsMap.get("sentiment");

        CompanyNewsItem cni = new CompanyNewsItem();
        cni.setId(id);
        cni.setRecordDateAsString(recordDate);
        try {
            cni.setRecordDate(sdf.parse(recordDate));
        } catch (Exception e) {
            logger.severe("Cannot convert recordDate from search to java date");
        }
        cni.setPublishedDateAsString(publishedDate);

        try {
            cni.setPublishedDate(sdf.parse(publishedDate));
        } catch (Exception e) {
            logger.severe("Cannot convert publishedDate from search to java date");
        }

        cni.setSubject(subject);
        cni.setBody(body);
        cni.setUrl(url);
        cni.setSentiment(sentiment);

        return cni;
    }

}
