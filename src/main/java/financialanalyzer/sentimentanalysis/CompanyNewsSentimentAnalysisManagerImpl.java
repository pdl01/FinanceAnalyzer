/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.sentimentanalysis;

import financialanalyzer.companynews.CompanyNewsServiceImpl;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import financialanalyzer.config.AppConfig;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class CompanyNewsSentimentAnalysisManagerImpl implements SentimentAnalysisManager {

    private static final Logger LOGGER = Logger.getLogger(CompanyNewsSentimentAnalysisManagerImpl.class.getName());

    @Autowired
    private AppConfig appConfig;

    private List<String> positiveCompanyNewsReference;
    private List<String> negativeCompanyNewsReference;

    private void loadPostiveCompanyNewsReference() {
        this.positiveCompanyNewsReference = new ArrayList<>();
        File dirPositive = new File(this.appConfig.getCompanyNewsPostiveSentimentAnalysisRefDir());
        File [] files = dirPositive.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){ //this line weeds out other directories/folders
                String s = this.readAllBytesJava7(files[i].getAbsolutePath());
                this.positiveCompanyNewsReference.add(s);
                //System.out.println(files[i]);
            }
        }
    
    }

    private void loadNegativeCompanyNewsReference() {

    }

    private String readAllBytesJava7(String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
           LOGGER.severe("Unable to read sentiment file:"+e.getMessage());
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
            totalX = totalX+itemX;
            LOGGER.info("similarity Score:" + itemX);

        }
        return totalX;
    }

    @Override
    public double getNegativeSentimentAnalysisIndex(String _input, String _dataSetId) {
        if (this.negativeCompanyNewsReference == null) {
            this.loadNegativeCompanyNewsReference();
        }
        return 0.0d;
    }

}
