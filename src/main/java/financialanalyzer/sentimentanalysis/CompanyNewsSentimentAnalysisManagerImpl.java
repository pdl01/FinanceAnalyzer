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
        cnsp.addIncludedUserRating(NewsItemRating.POSITIVE);
        cnsp.setNumResults(200);
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
        cnsp.addIncludedUserRating(NewsItemRating.NEGATIVE);

        cnsp.setNumResults(200);
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
        int numVerySimilarItems = 0;
        int numSimilarItems = 0;
        int numNotVerySimilarItems = 0;
        for (String refItem : this.positiveCompanyNewsReference) {
            JaccardSimilarity js = new JaccardSimilarity();
            Double itemX = js.apply(_input, refItem);
            totalX = totalX + itemX;
            if (itemX > 0.9) {
                numVerySimilarItems = numVerySimilarItems + 1;
            } else if (itemX >= 0.8 && itemX < 0.9) {
                numSimilarItems = numSimilarItems + 1;
            } else {
                numNotVerySimilarItems = numNotVerySimilarItems + 1;
            }
            LOGGER.info("positive similarity Score:" + itemX);

        }
        LOGGER.info("positive numberOfVerySimilarItems:" + numVerySimilarItems + " out of " + this.positiveCompanyNewsReference.size());
        LOGGER.info("positive numberOfSimilarItems:" + numSimilarItems + " out of " + this.positiveCompanyNewsReference.size());
        
        double similarityScore = 1.0d*(
                                      ( ((double)numVerySimilarItems / (double)positiveCompanyNewsReference.size() ) * 3.0) 
                                        + 
                                      (
                                        ((double)numSimilarItems / (double)positiveCompanyNewsReference.size() ) * 2.0)
                                      );
        return similarityScore;  
        //return totalX / this.positiveCompanyNewsReference.size();
        
        
        //return totalX;
    }

    @Override
    public double getNegativeSentimentAnalysisIndex(String _input, String _dataSetId) {
        if (this.negativeCompanyNewsReference == null) {
            this.loadNegativeCompanyNewsReference();
        }
        double totalX = 0.0;
        int numVerySimilarItems = 0;
        int numSimilarItems = 0;
        int numNotVerySimilarItems = 0;
        for (String refItem : this.negativeCompanyNewsReference) {
            JaccardSimilarity js = new JaccardSimilarity();
            Double itemX = js.apply(_input, refItem);
            totalX = totalX + itemX;
            if (itemX >= 0.90) {
                numVerySimilarItems = numVerySimilarItems + 1;
            } else if (itemX >= 0.8 && itemX < 0.9) {
                numSimilarItems = numSimilarItems + 1;
            } else {
                numNotVerySimilarItems = numNotVerySimilarItems + 1;
            }
            LOGGER.info("negative similarity Score:" + itemX);
        }
        LOGGER.info("negative numberOfVerySimilarItems:" + numVerySimilarItems + " out of " + this.negativeCompanyNewsReference.size());
        LOGGER.info("negative numberOfSimilarItems:" + numSimilarItems + " out of " + this.negativeCompanyNewsReference.size());
        double similarityScore = 1.0d*((((double)numVerySimilarItems / (double)negativeCompanyNewsReference.size() ) * 3.0) + (((double)numSimilarItems / (double)negativeCompanyNewsReference.size() ) * 2.0));
        
        return similarityScore;  

        //return totalX / this.negativeCompanyNewsReference.size();
    }

    @Override
    public boolean doesNewsArticleRelateToSymbol(CompanyNewsItem _cni) {

        return _cni.getBody().toLowerCase().contains(_cni.getSymbol().toLowerCase());

    }

    private boolean isComparisonSetSuitable(List<String> _items) {
        if (_items == null || (_items != null && _items.size() < 150)) {
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
        if (this.negativeCompanyNewsReference == null) {
            this.loadNegativeCompanyNewsReference();
        }
        if (!this.isComparisonSetSuitable(this.positiveCompanyNewsReference)) {
            return null;
        }

        if (!this.isComparisonSetSuitable(this.negativeCompanyNewsReference)) {
            return null;
        }

        double positiveScore = this.getPositiveSentimentAnalysisIndex(_item.getBody(), null);
        double negativeScore = this.getNegativeSentimentAnalysisIndex(_item.getBody(),null);
        LOGGER.debug("Positive Index = " + positiveScore + " for " + _item.getId());
        LOGGER.debug("Negative Index = " + negativeScore + " for " + _item.getId());

        //TODO: perform sentimentanalsysis
        
        if (positiveScore > negativeScore) {
            return NewsItemRating.POSITIVE;
        } else {
            return NewsItemRating.NEGATIVE;
        }
        
    }

    @Override
    public String getVersion() {
        return "0.0.0";
    }

    @Override
    public void clearCache() {
        if (this.positiveCompanyNewsReference != null) {
            this.positiveCompanyNewsReference = null;
        }
        if (this.negativeCompanyNewsReference != null) {
            this.negativeCompanyNewsReference = null;
        }
    }

}
