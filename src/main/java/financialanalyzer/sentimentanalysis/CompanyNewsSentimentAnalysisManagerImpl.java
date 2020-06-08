/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.sentimentanalysis;

import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.CompanyNewsRepo;
import financialanalyzer.companynews.CompanyNewsSearchProperties;
import financialanalyzer.companynews.CompanyNewsService;
import financialanalyzer.companynews.CompanyNewsServiceImpl;
import financialanalyzer.companynews.NewsItemRating;
import java.util.List;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import financialanalyzer.config.AppConfig;
import financialanalyzer.objects.Company;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class CompanyNewsSentimentAnalysisManagerImpl implements SentimentAnalysisManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyNewsSentimentAnalysisManagerImpl.class.getName());

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CompanyNewsService companyNewsServiceImpl;

    @Autowired
    private CompanyNewsRepo companyNewsSearchRepo;

    private List<String> positiveCompanyNewsReference;
    private List<String> negativeCompanyNewsReference;

    private void loadPostiveCompanyNewsReference() {
        this.positiveCompanyNewsReference = new ArrayList<>();
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        cnsp.setUserRating(NewsItemRating.POSITIVE);

        cnsp.setNumResults(100);
        cnsp.setSortField("recordDate");
        cnsp.setSortOrder("DESC");

        List<CompanyNewsItem> items = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (items != null && !items.isEmpty()) {
            for (CompanyNewsItem item : items) {
                this.positiveCompanyNewsReference.add(item.getBody());
            }
        }

    }

    private void loadNegativeCompanyNewsReference() {
        this.negativeCompanyNewsReference = new ArrayList<>();
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        cnsp.setUserRating(NewsItemRating.POSITIVE);

        cnsp.setNumResults(100);
        cnsp.setSortField("recordDate");
        cnsp.setSortOrder("DESC");

        List<CompanyNewsItem> items = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (items != null && !items.isEmpty()) {
            for (CompanyNewsItem item : items) {
                this.negativeCompanyNewsReference.add(item.getBody());
            }
        }

    }

    private String readAllBytesJava7(String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            LOGGER.error("Unable to read sentiment file:" + e.getMessage());
        }
        return content;
    }

    @Override
    public double getPositiveSentimentAnalysisIndex(String _input, String _dataSetId) {
        if (this.positiveCompanyNewsReference == null) {
            this.loadPostiveCompanyNewsReference();
        }
        double totalX = 0.0;
        for (String refItem : this.positiveCompanyNewsReference) {
            JaccardSimilarity js = new JaccardSimilarity();
            Double itemX = js.apply(_input, refItem);
            totalX = totalX + itemX;
            LOGGER.info("similarity Score:" + itemX);

        }
        return totalX / this.positiveCompanyNewsReference.size();
        //return totalX;
    }

    @Override
    public double getNegativeSentimentAnalysisIndex(String _input, String _dataSetId) {
        if (this.negativeCompanyNewsReference == null) {
            this.loadNegativeCompanyNewsReference();
        }
        double totalX = 0.0;
        for (String refItem : this.negativeCompanyNewsReference) {
            JaccardSimilarity js = new JaccardSimilarity();
            Double itemX = js.apply(_input, refItem);
            totalX = totalX + itemX;
            LOGGER.info("similarity Score:" + itemX);

        }
        return totalX / this.positiveCompanyNewsReference.size();
    }

    @Override
    public boolean doesNewsArticleRelateToSymbol(CompanyNewsItem _cni) {

        return _cni.getBody().toLowerCase().contains(_cni.getSymbol().toLowerCase());

    }

    private boolean isComparisonSetSuitable(List<String> _items) {
        if (_items == null || (_items != null && _items.size() < 200)) {
            //not enough data to compare to
            return false;
        }
        return true;
    }

    @Override
    public NewsItemRating developSystemRating(CompanyNewsItem _item) {
        if (!doesNewsArticleRelateToSymbol(_item)) {
            return NewsItemRating.UNRELATED;
        }
        if (this.positiveCompanyNewsReference == null) {
            this.loadPostiveCompanyNewsReference();
        }

        if (!this.isComparisonSetSuitable(this.positiveCompanyNewsReference)) {
            return null;
        }

        if (!this.isComparisonSetSuitable(this.negativeCompanyNewsReference)) {
            return null;
        }
        
        LOGGER.debug("Positive Index = " + this.getPositiveSentimentAnalysisIndex(_item.getBody(), null) + " for " + _item.getId());
        LOGGER.debug("Negative Index = " + this.getNegativeSentimentAnalysisIndex(_item.getBody(), null) + " for " + _item.getId());

        //TODO: perform sentimentanalsysis
        return null;
    }

    @Override
    public String getVersion() {
        return "0.0.0";
    }

}
