package financialanalyzer.controller.v1;

import financialanalyzer.companynames.CompanyRepo;
import financialanalyzer.companynews.CompanyNewsDownloadDriver;
import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.CompanyNewsRepo;
import financialanalyzer.companynews.CompanyNewsSearchProperties;
import financialanalyzer.companynews.CompanyNewsService;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author phil
 */
@RestController
@RequestMapping("/api/v1/news")
public class NewsRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsRestController.class.getName());
    @Autowired
    private CompanyRepo companySearchRepo;
    @Autowired
    private CompanyNewsService companyNewsServiceImpl;
    
    @Autowired
    private CompanyNewsRepo companyNewsSearchRepo;
    
    @Autowired
    private CompanyNewsDownloadDriver companyNewsDownloadDriver;
    
    @RequestMapping(value = "/fetchLatestData", method = RequestMethod.POST, produces = "application/json")
    public RestResponse triggerNewsDownload() {
        RestResponse restResponse = new RestResponse();
        this.companyNewsDownloadDriver.fetchLatestData(new Date());
        //restResponse.setObject(company);
        return restResponse;
    }

    @RequestMapping(value = "/latest/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsStartingWith(@PathVariable("start") int _start) {
        return this.getNewsStartingWithInRange(_start, 25);
    }
    
    @RequestMapping(value = "/latest/{start}{numberOfItems}/", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsStartingWithInRange(@PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        //csp.setCompanyId(_id);

        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();
        
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        //cnsp.setStockExchange(company.getStockExchange());
        //cnsp.setStockSymbol(company.getStockSymbol());
        cnsp.setSortField("recordDate");
        cnsp.setSortOrder("DESC");
        cnsp.setStartResults(0);
        cnsp.setNumResults(_numberOfItems);

        //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
        List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (cnis != null) {
            companyNewsItems.addAll(cnis);
        }
        restResponse.setObject(companyNewsItems);
        return restResponse;

    }
    
    @RequestMapping(value = "/latest", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNews() {
        return this.getNewsStartingWith(0);
    }
    
    @RequestMapping(value = "/sector/{sector}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsBySector(@PathVariable("sector") String _sector) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        //csp.setCompanyId(_id);

        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();
        
        if (companies != null) {
            for (Company company : companies) {
                CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
                cnsp.setStockExchange(company.getStockExchange());
                cnsp.setStockSymbol(company.getStockSymbol());
                cnsp.setSortField("recordDate");

                //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
                List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
                if (cnis != null) {
                    companyNewsItems.addAll(cnis);
                }
            }
        }
        restResponse.setObject(companyNewsItems);
        return restResponse;
        
    }
    
    @RequestMapping(value = "/company/{id}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompanyNewsForCompanyStartingWith(@PathVariable("id") String _id, @PathVariable("start") int _start) {
        return this.getCompanyNewsForCompanyStartingWithInRange(_id, _start, 25);
    }
    
    @RequestMapping(value = "/company/{id}/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompanyNewsForCompanyStartingWithInRange(@PathVariable("id") String _id, @PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setCompanyId(_id);
        csp.setStartResults(0);
        csp.setNumResults(_numberOfItems);
        
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();
        
        if (companies != null) {
            for (Company company : companies) {
                CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
                cnsp.setStockExchange(company.getStockExchange());
                cnsp.setStockSymbol(company.getStockSymbol());
                cnsp.setSortField("recordDate");

                //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
                List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
                if (cnis != null) {
                    companyNewsItems.addAll(cnis);
                }
            }
        }
        restResponse.setObject(companyNewsItems);
        return restResponse;
        
    }
    
    @RequestMapping(value = "/company/{id}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompanyNewsForCompany(@PathVariable("id") String _id) {
        return this.getCompanyNewsForCompanyStartingWith(_id, 0);
        
    }
}
