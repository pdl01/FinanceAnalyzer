/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.sentimentanalysis.CompanyNewsSentimentAnalysisManagerImpl;
import financialanalyzer.sentimentanalysis.SentimentAnalysisManager;
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
 * @author phil
 */
@Component
public class SentimentAnalysisReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalysisReceiver.class.getName());

    @Autowired
    private CompanyNewsService companyNewsServiceImpl;

    @Autowired
    private SentimentAnalysisManager companyNewsSentimentAnalysisManagerImpl;

    @JmsListener(destination = ActiveMQConfig.NEWS_SENTIMENT_RATING_QUEUE)
    public void receiveMessage(@Payload Company _company,
            @Headers MessageHeaders headers,
            Message message, Session session) {

        LOGGER.info("Receiving company:" + _company.getId());
        //get all items for the company with no system rating
        List<CompanyNewsItem> cnis = this.companyNewsServiceImpl.getNewsItemsWithNoSystemRatingForCompany(_company);
        if (cnis != null) {
            LOGGER.info("Number of news items with no system rating to process for sentiment analysis:"+cnis.size());
            //perform analysis on those
            for (CompanyNewsItem item : cnis) {
                NewsItemRating systemRating = this.companyNewsSentimentAnalysisManagerImpl.developSystemRating(item);
                if (systemRating != null) {
                    this.companyNewsServiceImpl.updateSystemRating(item.getId(), systemRating);
                }
            }
        } else {
            LOGGER.info("No news items to process.");
        }

    }
}
