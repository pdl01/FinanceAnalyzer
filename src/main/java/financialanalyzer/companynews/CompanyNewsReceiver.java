/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.systemactivity.SystemActivityManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyNewsReceiver.class.getName());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private CompanyNewsRepo companyNewsSearchRepo;

    @Autowired
    private CompanyNewsService companyNewsServiceImpl;

    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

    @JmsListener(destination = ActiveMQConfig.COMPANY_NEWS_QUEUE)
    public void receiveMessage(@Payload Company _company,
            @Headers MessageHeaders headers,
            Message message, Session session) {
        LOGGER.info("Received " + _company.getId());
        //get top 10 news urls about the company

        List<CompanyNewsItem> cnis = this.companyNewsServiceImpl.fetchCompanyNewsItems(_company, 10);
        boolean addedNewsItem = false;
        int submissionCounter = 0;
        if (cnis != null && cnis.size() > 0) {
            for (CompanyNewsItem cni : cnis) {
                //check if it exists already; if it does don't update
                CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
                cnsp.setCompanyNewsItemId(cni.getId());

                List<CompanyNewsItem> alreadyIngestedNewsItems = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
                if (alreadyIngestedNewsItems == null || (alreadyIngestedNewsItems != null && alreadyIngestedNewsItems.size() == 0)) {
                    addedNewsItem = true;
                    submissionCounter++;
                    cni.setRecordDate(new Date());
                    cni.setPublishedDate(new Date());
                    this.companyNewsSearchRepo.submit(cni);

                    //TODO:trigger sentimient analysis update
                }
            }

        }
        if (addedNewsItem) {
            this.systemActivityManagerImpl.saveSystemActivity(_company.getStockSymbol(), _company.getStockExchange(), SystemActivityManager.ACTIVITY_TYPE_COMPANY_NEWS, "acquired " + submissionCounter + " news items");
            this.companyNewsServiceImpl.submitCompanyToSentimentAnalysisQueue(_company);
        }

        //save the url and text and do some sentiment analysis (good/bad news)
        //save the item to the repo
        //this.companyNewsSearchRepo.submit(_companyNewsItem);
    }
}
