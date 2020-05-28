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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsServiceImpl implements CompanyNewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyNewsServiceImpl.class.getName());

    @Autowired
    private SentimentAnalysisManager companyNewsSentimentAnalysisManagerImpl;

    @Autowired
    private CompanyNewsProviderRegistry companyNewProviderRegistry;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private CompanyNewsRepo companyNewsSearchRepo;

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
                        LOGGER.error(e.getMessage());
                    }

                    if (cnisItemClone != null) {
                        cnisItemClone.setId(_company.getStockExchange() + ":" + _company.getStockSymbol() + ":" + cnisItemClone.getUrl());
                        cnisItemClone.setExchange(_company.getStockExchange());
                        cnisItemClone.setSymbol(_company.getStockSymbol());
                        cnisItemClone.setSystemRating(NewsItemRating.NONE);
                        cnisItemClone.setUserRating(NewsItemRating.NONE);
                        cnis.add(cnisItemClone);

                    }

                   
                }

            }
            //cnis.addAll(cnis_items);
        }

        return cnis;
    }

    @Override
    public void submitCompanyToDownloadQueue(Company _company) {
        this.jmsTemplate.convertAndSend(ActiveMQConfig.COMPANY_NEWS_QUEUE, _company);
    }

    @Override
    public void submitCompanyToSentimentAnalysisQueue(Company _company) {
        this.jmsTemplate.convertAndSend(ActiveMQConfig.NEWS_SENTIMENT_RATING_QUEUE, _company);
    }

    @Override
    public void updateUserRating(String _id, NewsItemRating _nir) {
        CompanyNewsItem cni = this.getCompanyNewsItem(_id);
        if (cni == null) {
            return;
        }
        cni.setUserRating(_nir);
        this.companyNewsSearchRepo.updateUserRatingForNewsItem(cni);
    }

    private CompanyNewsItem getCompanyNewsItem(String _id) {
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        cnsp.setCompanyNewsItemId(_id);
        List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (cnis != null && cnis.size() > 0) {
            return cnis.get(0);
        }
        return null;
    }

    @Override
    public void updateSystemRating(String _id, NewsItemRating _nir) {
        CompanyNewsItem cni = this.getCompanyNewsItem(_id);
        if (cni == null) {
            LOGGER.warn("Company News Item not found:" + _id);
            return;
        }
        cni.setSystemRating(_nir);
        this.companyNewsSearchRepo.updateSystemRatingForNewsItems(cni);
    }

    @Override
    public List<Company> getCompaniesThatHaveNewsItemsWithNoSystemRating() {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CompanyNewsItem> getNewsItemsWithNoSystemRatingForCompany(Company _company) {
        LOGGER.debug("Entering getNewsItemsWithNoSystemRatingForCompany:" + _company.getId());
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        cnsp.setSystemRating(NewsItemRating.NONE);
        cnsp.setStockSymbol(_company.getStockSymbol());
        cnsp.setStockExchange(_company.getStockExchange());
        List<CompanyNewsItem> returnItems = new ArrayList<>();
        List<CompanyNewsItem> searchItems = new ArrayList<>();
        int start = 0;
        int numToRturn = 200;
        cnsp.setNumResults(numToRturn);
        cnsp.setStartResults(start);
        boolean finished = false;
        while (!finished) {

            searchItems = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);

            if (searchItems == null || (searchItems != null && searchItems.size() == 0)) {
                finished = true;
            } else {
                LOGGER.debug("Found " + searchItems.size() + " items");
                for (CompanyNewsItem cni : searchItems) {
                    if (cni.getSystemRating() == NewsItemRating.NONE) {
                        returnItems.add(cni);
                    }
                }
                //returnItems.addAll(searchItems);
                start = start + searchItems.size();
                cnsp.setStartResults(start);
            }

        }
        LOGGER.debug("Exiting getNewsItemsWithNoSystemRatingForCompany");
        return returnItems;
    }

    @Override
    public List<Company> getCompaniesThatHaveNoNewsItems() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
