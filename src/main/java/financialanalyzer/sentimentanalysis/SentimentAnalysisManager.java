/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.sentimentanalysis;

import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public interface SentimentAnalysisManager {
    public double getPositiveSentimentAnalysisIndex(String _input,String _dataSetId); 
    public double getNegativeSentimentAnalysisIndex(String _input,String _dataSetId); 

}
