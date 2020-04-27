/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.jms.Message;
import javax.jms.Session;
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

    private static final Logger LOGGER = Logger.getLogger(CompanyNewsReceiver.class.getName());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private CompanyNewsRepo companyNewsSearchRepo;

    @Autowired
    private CompanyNewsService companyNewsServiceImpl;

    @JmsListener(destination = ActiveMQConfig.COMPANY_NEWS_QUEUE)
    public void receiveMessage(@Payload Company _company,
            @Headers MessageHeaders headers,
            Message message, Session session) {
        LOGGER.info("Received " + _company.getId());
        //get top 10 news urls about the company

        List<CompanyNewsItem> cnis = this.companyNewsServiceImpl.fetchCompanyNewsItems(_company, 10);

        if (cnis != null && cnis.size() > 0) {
            for (CompanyNewsItem cni : cnis) {
                //check if it exists already; if it does don't update
                CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
                cnsp.setCompanyNewsItemId(cni.getId());

                List<CompanyNewsItem> alreadyIngestedNewsItems = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
                if (alreadyIngestedNewsItems == null || (alreadyIngestedNewsItems != null && alreadyIngestedNewsItems.size() == 0)) {
                    cni.setRecordDate(new Date());
                    cni.setPublishedDate(new Date());
                    this.companyNewsSearchRepo.submit(cni);
                    //TODO:trigger sentimient analysis update
                }
            }

        }
        //save the url and text and do some sentiment analysis (good/bad news)
        //save the item to the repo

        //this.companyNewsSearchRepo.submit(_companyNewsItem);
    }
}
