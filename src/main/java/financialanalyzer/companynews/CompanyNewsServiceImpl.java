/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.config.ActiveMQConfig;
import financialanalyzer.http.HTMLPage;
import financialanalyzer.http.HttpFetcher;
import financialanalyzer.objects.Company;
import financialanalyzer.sentimentanalysis.SentimentAnalysisManager;
import financialanalyzer.systemactivity.SystemActivityManager;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

    @Autowired
    protected HttpFetcher httpFetcher;

    private String version = "1.0.0";
    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

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
                        cnisItemClone.setIndustries(_company.getIndustries());
                        cnisItemClone.setSectors(_company.getSectors());
                        cnisItemClone.setEnhancementVersion(version);
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
        this.systemActivityManagerImpl.saveSystemActivity(_company.getStockSymbol(), _company.getStockExchange(), SystemActivityManager.ACTIVITY_TYPE_COMPANY_NEWS, "enqueuing to download news");
        this.jmsTemplate.convertAndSend(ActiveMQConfig.COMPANY_NEWS_QUEUE, _company);
    }

    @Override
    public void submitCompanyToSentimentAnalysisQueue(Company _company) {
        this.systemActivityManagerImpl.saveSystemActivity(_company.getStockSymbol(), _company.getStockExchange(), SystemActivityManager.ACTIVITY_TYPE_COMPANY_NEWS, "enqueuing to sentiment analysis");
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
    public void updateSystemRating(String _id, NewsItemRating _nir,String _ratingSystemsVersion) {
        CompanyNewsItem cni = this.getCompanyNewsItem(_id);
        if (cni == null) {
            LOGGER.warn("Company News Item not found:" + _id);
            return;
        }
        cni.setSystemRating(_nir);
        cni.setSystemRatingVersion(_ratingSystemsVersion);
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

    @Override
    public CompanyNewsItem buildCompanyNewsItemFromURL(String _url) {

        if (_url.contains("https://www.bloomberg.com")) {
            return null;
        }

        HTMLPage companyNewsItemPage = this.httpFetcher.getResponse(_url, false);
        if (companyNewsItemPage != null && companyNewsItemPage.getContent() != null && !companyNewsItemPage.getContent().isEmpty()) {
            CompanyNewsItem cni = new CompanyNewsItem();

            cni.setUrl(_url);

            //LOGGER.info(companyNewsItemPage.getContent());
            Document newsDoc = Jsoup.parse(companyNewsItemPage.getContent());
            Element newsTitle = newsDoc.selectFirst("title");
            String newsTitleText = "";
            if (newsTitle != null) {
                newsTitleText = newsTitle.text();
            }
            if (newsTitleText.trim().isEmpty()) {
                newsTitleText = "Empty Title";
            }
            
            cni.setSubject(newsTitleText);
            //LOGGER.info(newsTitleText);
            String bodyText = newsDoc.body().text();
            String newsBodyText = "Empty";

            if (bodyText != null && bodyText.length() > 2000) {
                newsBodyText = bodyText;
            } else if (bodyText != null) {
                newsBodyText = bodyText;
            }
            cni.setBody(bodyText);
            //LOGGER.info(newsBodyText);
            return cni;
        }
        return null;
    }

}
