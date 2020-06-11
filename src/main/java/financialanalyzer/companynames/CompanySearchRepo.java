/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import financialanalyzer.companynames.CompanyRepo;
import financialanalyzer.elasticsearch.ElasticSearchManager;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import java.io.IOException;
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
public class CompanySearchRepo extends ElasticSearchManager implements CompanyRepo {

    private static final Logger logger = LoggerFactory.getLogger(CompanySearchRepo.class.getName());

    @Override
    public Company submit(Company _company) {
        if (_company == null) {
            return null;
        }
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return null;
        }

        IndexRequest indexRequest = new IndexRequest("companies", "company", _company.getId())
                .source("id", _company.getId(),
                        "name", _company.getName(), "symbol", _company.getStockSymbol(),
                        "exchange", _company.getStockExchange(),
                        "sector", _company.getSectors(),
                        "industry",_company.getIndustries(),
                        "enhancementVersion",_company.getEnhancementVersion()
                );

        try {
            IndexResponse indexResponse = client.index(indexRequest);
            //logger.info(indexResponse.getIndex());
            //logger.info(indexResponse.getResult().name());
        } catch (IOException ex) {
            //ex.printStackTrace();
            logger.error(ex.getMessage());
        }
        this.closeClient(client);
        return _company;
    }

    @Override
    public boolean delete(Company _company) {
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return false;
        }
        DeleteRequest request = new DeleteRequest("companies", "company", _company.getId());

        try {
            RequestOptions options = null;
            client.delete(request, options);
            //CreateIndexRequest request = new CreateIndexRequest("users");
            //client.
            //CreateIndexResponse  createIndexResponse = client.indices().indices().create(request);
        } catch (IOException ex) {
            logger.error("Error when deleting company " + ex.getMessage());

        } catch (Exception ex) {
            logger.error("Error when deleting company " + ex.getMessage());

        }

        this.closeClient(client);
        return true;
    }

    @Override
    public List<Company> searchForCompany(CompanySearchProperties _csp) {
        logger.info("Beginning searchForCompany");
        List<Company> companies = new ArrayList<>();
        RestHighLevelClient client = this.buildClient();
        if (client == null) {
            return companies;
        }

        SearchRequest searchRequest = new SearchRequest("companies");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        QueryBuilder matchQueryBuilder = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        if (_csp.getCompanyName() != null) {
            boolQuery.must(QueryBuilders.matchQuery("name", _csp.getCompanyName()));
        } else if (_csp.getStockExchange() != null) {
            boolQuery.must(QueryBuilders.matchQuery("exchange", _csp.getStockExchange()));
            
        } else if (_csp.getStockSymbol() != null) {
            boolQuery.must(QueryBuilders.matchQuery("symbol", _csp.getStockSymbol()));

        } else if (_csp.getCompanyId() != null) {
            boolQuery.must(QueryBuilders.matchQuery("_id", _csp.getCompanyId()));
        }

        if (_csp.getIndustries()!= null) {
            BoolQueryBuilder industryQuery = QueryBuilders.boolQuery();
            for (String industry : _csp.getIndustries()) {
                industryQuery.should(QueryBuilders.matchQuery("industry", industry));
            }
            boolQuery.must(industryQuery);

        }
        if (_csp.getSectors()!= null) {
            BoolQueryBuilder sectorQuery = QueryBuilders.boolQuery();
            for (String sector : _csp.getSectors()) {
                sectorQuery.should(QueryBuilders.matchQuery("sector", sector));
            }
            boolQuery.must(sectorQuery);
        }
        

        //.fuzziness(Fuzziness.AUTO);
        searchSourceBuilder.query(boolQuery).from(_csp.getStartResults()).size(_csp.getNumResults());

        /*        if (_searchProperties.getSortField() != null) {
            //TODO sort based on dimension type
            if ("ASC".equalsIgnoreCase(_searchProperties.getSortOrder())) {
                searchSourceBuilder.sort(_searchProperties.getSortField(), SortOrder.ASC);
            } else {
                searchSourceBuilder.sort(_searchProperties.getSortField(), SortOrder.DESC);
            }
        }
         */
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                //build some artificial items that will house basic info about the artifact, without hitting the main db again. (id,title)
                if (hit.getType().equalsIgnoreCase("company")) {
                    String sourceAsString = hit.getSourceAsString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    Company company = this.buildCompanyFromSourceMap(sourceAsMap);

                    companies.add(company);
                }
                // do something with the SearchHit
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        this.closeClient(client);
        logger.info("Completed searchForCompany");

        return companies;

    }

    private Company buildCompanyFromSourceMap(Map<String, Object> _sourceAsMap) {
        String id = (String) _sourceAsMap.get("id");

        String name = (String) _sourceAsMap.get("name");
        String symbol = (String) _sourceAsMap.get("symbol");
        String exchange = (String) _sourceAsMap.get("exchange");
        List<String> sectors = (List<String>) _sourceAsMap.get("sector");
        List<String> industries = (List<String>) _sourceAsMap.get("industry");
        String enhancementVersion = (String)_sourceAsMap.get("enhancementVersion");
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setStockExchange(exchange);
        company.setStockSymbol(symbol);
        company.setSectors(sectors);
        company.setIndustries(industries);
        company.setEnhancementVersion(enhancementVersion);
        
        return company;
    }

}
