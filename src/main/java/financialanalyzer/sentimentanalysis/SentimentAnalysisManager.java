/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.sentimentanalysis;

import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.NewsItemRating;
import financialanalyzer.objects.Company;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public interface SentimentAnalysisManager {
    
    public String getVersion();
    
    public NewsItemRating developSystemRating(CompanyNewsItem _item);
    public double getPositiveSentimentAnalysisIndex(String _input, String _dataSetId);

    public double getNegativeSentimentAnalysisIndex(String _input, String _dataSetId);

    public boolean doesNewsArticleRelateToSymbol(CompanyNewsItem _cni);
    public void clearCache();
}
