/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.objects.Company;
import financialanalyzer.sentimentanalysis.SentimentAnalysisManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsServiceImpl implements CompanyNewsService {

    private static final Logger LOGGER = Logger.getLogger(CompanyNewsServiceImpl.class.getName());

    @Autowired
    private SentimentAnalysisManager companyNewsSentimentAnalysisManagerImpl;

    @Autowired
    private CompanyNewsProviderRegistry companyNewProviderRegistry;

    @Autowired
    private JmsTemplate jmsTemplate;    
    
    @Override
    public List<CompanyNewsItem> fetchCompanyNewsItems(Company _company, int _numberOfArticlesPerProvider) {

        List<CompanyNewsItem> cnis = new ArrayList<>();

        for (CompanyNewsProvider provider : this.companyNewProviderRegistry.getProviders()) {
            List<CompanyNewsItem> cnis_items = provider.getCompanyNewsItems(_company, _numberOfArticlesPerProvider);
            if (cnis_items != null) {
                for (CompanyNewsItem cnis_item : cnis_items) {
                    //TODO:move to new driver
                    //Double d = this.companyNewsSentimentAnalysisManagerImpl.getPositiveSentimentAnalysisIndex(cnis_item.getBody(), null);
                    //LOGGER.info("Positive Sentiment:" + d);
                    CompanyNewsItem cnisItemClone = null;
                    try {
                        cnisItemClone = (CompanyNewsItem) cnis_item.clone();
                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                    }

                    if (cnisItemClone != null) {
                        cnisItemClone.setId(_company.getStockExchange() + ":" + _company.getStockSymbol() + ":" + cnisItemClone.getUrl());
                        cnisItemClone.setExchange(_company.getStockExchange());
                        cnisItemClone.setSymbol(_company.getStockSymbol());
                        cnis.add(cnisItemClone);
                    }

                }
                //cnis.addAll(cnis_items);
            }
        }

        return cnis;
    }

    @Override
    public void submitCompanyToDownloadQueue(Company _company) {
        this.jmsTemplate.convertAndSend(ActiveMQConfig.COMPANY_NEWS_QUEUE, _company);
    }

}
