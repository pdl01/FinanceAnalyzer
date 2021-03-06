
package financialanalyzer.companynews;

import financialanalyzer.http.HTMLPage;
import financialanalyzer.http.HttpFetcher;
import financialanalyzer.objects.Company;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class BingCompanyNewsProvider implements CompanyNewsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BingCompanyNewsProvider.class.getName());
    public static final String PROVIDER_IDENTIFIER = "bing";

    @Autowired
    protected HttpFetcher httpFetcher;

    @Autowired 
    protected CompanyNewsService companyNewsServiceImpl;
    
    @Override
    public List<CompanyNewsItem> getCompanyNewsItems(Company _company, int _numberOfArticles) {
        LOGGER.info(this.getIdentifier()+":beginning getCompanyNewsItems");
        String url = "https://www.bing.com/news/search?q=::ENCODED_COMPANY_NAME::+stock";
        List<CompanyNewsItem> cnis = new ArrayList<>();

        HTMLPage companyNewsIndexPage = null;
        
        String encodedCompanyName = _company.getName().replaceAll(" ", "+");
        String resolvedurl = url.replaceAll("::ENCODED_COMPANY_NAME::", encodedCompanyName);
        try {
            LOGGER.info("Starting Download of company News for :" + _company.getStockSymbol());
            companyNewsIndexPage = this.httpFetcher.getResponse(resolvedurl, false);
            //LOGGER.info(companyNewsIndexPage.getContent());
            if (companyNewsIndexPage != null && companyNewsIndexPage.getContent() != null) {
                Document doc = Jsoup.parse(companyNewsIndexPage.getContent());
                Elements dbsr_divs = doc.select("div.t_t");
                if (dbsr_divs != null && dbsr_divs.size() > 0) {
                    Iterator<Element> dbsrDivsIterator = dbsr_divs.iterator();
                    while (dbsrDivsIterator.hasNext()) {
                        Element dbsrDiv = dbsrDivsIterator.next();
                        Element dbsrLink = dbsrDiv.selectFirst("a[href]");
                        if (dbsrLink != null) {
                            String dbsrLinkHref = dbsrLink.attr("href");
                            //download the link to get the title, and text
                            LOGGER.info(dbsrLinkHref);
                            //download the link to get the title, and text
                            CompanyNewsItem item = this.companyNewsServiceImpl.buildCompanyNewsItemFromURL(dbsrLinkHref);
                            if (item != null) {
                                cnis.add(item);
                            }
                            
                        }
                    }
                }
            }

            LOGGER.info("Completed Download of company News index for :" + _company.getStockSymbol());

        } catch (Exception ex) {
            LOGGER.error("Exception fetching company news for:" + _company.getStockSymbol(), ex);
        }

        return cnis;
    }

    @Override
    public String getIdentifier() {
        return BingCompanyNewsProvider.PROVIDER_IDENTIFIER;
    }
    
}
