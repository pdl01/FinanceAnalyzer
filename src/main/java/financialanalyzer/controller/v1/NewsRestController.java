package financialanalyzer.controller.v1;

import financialanalyzer.companynames.CompanyRepo;
import financialanalyzer.companynews.CompanyNewsDownloadDriver;
import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.CompanyNewsRepo;
import financialanalyzer.companynews.CompanyNewsSearchProperties;
import financialanalyzer.companynews.CompanyNewsService;
import financialanalyzer.companynews.NewsItemRating;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.objects.NewsItemForm;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    public RestResponse getLatestNewsStartingWith(@PathVariable("start") int _start) {
        return this.getLatestNewsStartingWithInRange(_start, 25);
    }

    @RequestMapping(value = "/latest/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getLatestNewsStartingWithInRange(@PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        RestResponse restResponse = new RestResponse();
        //CompanySearchProperties csp = new CompanySearchProperties();
        //csp.setCompanyId(_id);

        //List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();

        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        //cnsp.setStockExchange(company.getStockExchange());
        //cnsp.setStockSymbol(company.getStockSymbol());
        cnsp.setSortField("recordDate");
        cnsp.setSortOrder("DESC");
        cnsp.setStartResults(_start);
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
    public RestResponse getLatestNews() {
        return this.getLatestNewsStartingWith(0);
    }

    @RequestMapping(value = "/sector/{sector}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsBySector(@PathVariable("sector") String _sector) {
        return this.getNewsBySectorStartingWith(_sector, 0);

    }

    @RequestMapping(value = "/sector/{sector}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsBySectorStartingWith(@PathVariable("sector") String _sector, @PathVariable("start") int _start) {
        return this.getNewsBySectorStartingWithInRange(_sector, 0, 20);
    }

    @RequestMapping(value = "/sector/{sector}/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsBySectorStartingWithInRange(@PathVariable("sector") String _sector, @PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();
        
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        ArrayList<String> sectors = new ArrayList<>();
        sectors.add(_sector);
        cnsp.setSectors(sectors);
        cnsp.setSortField("recordDate");
        cnsp.setStartResults(_start);
        cnsp.setNumResults(_numberOfItems);
        //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
        List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (cnis != null) {
            companyNewsItems.addAll(cnis);
        }

        RestResponse returnResponse = new RestResponse();
        returnResponse.setCode(0);
        returnResponse.setObject(companyNewsItems);
        return returnResponse;
    }

    @RequestMapping(value = "/industry/{industry}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsByIndustry(@PathVariable("industry") String _industry) {
        return this.getNewsByIndustryStartingWith(_industry, 0);

    }

    @RequestMapping(value = "/industry/{industry}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsByIndustryStartingWith(@PathVariable("industry") String _industry, @PathVariable("start") int _start) {
        return this.getNewsByIndustryStartingWithInRange(_industry, 0, 20);
    }

    @RequestMapping(value = "/industry/{industry}/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsByIndustryStartingWithInRange(@PathVariable("industry") String _industry, @PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();
        
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        ArrayList<String> industries = new ArrayList<>();
        industries.add(_industry);
        cnsp.setIndustries(industries);
        cnsp.setSortField("recordDate");
        cnsp.setStartResults(_start);
        cnsp.setNumResults(_numberOfItems);
        //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
        List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (cnis != null) {
            companyNewsItems.addAll(cnis);
        }

        RestResponse returnResponse = new RestResponse();
        returnResponse.setCode(0);
        returnResponse.setObject(companyNewsItems);
        return returnResponse;
    }

    @RequestMapping(value = "/company/{id}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompanyNewsForCompanyStartingWith(@PathVariable("id") String _id, @PathVariable("start") int _start) {
        return this.getNewsForCompanyStartingWithInRange(_id, _start, 25);
    }

    @RequestMapping(value = "/company/{id}/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsForCompanyStartingWithInRange(@PathVariable("id") String _id, @PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {

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
                cnsp.setStartResults(_start);
                cnsp.setNumResults(_numberOfItems);
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
    public RestResponse getNewsForCompany(@PathVariable("id") String _id) {
        return this.getCompanyNewsForCompanyStartingWith(_id, 0);

    }

    @RequestMapping(value = "/symbol/{symbol}/fetch", method = RequestMethod.POST, produces = "application/json")
    public RestResponse fetchNewsForCompany(@PathVariable("symbol") String symbol) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setStockSymbol(symbol);
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        if (companies != null) {
            companies.forEach(company_item -> {
                this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company_item);
                //this.stockHistoryDownloadServiceImpl.queueCompanyForFetch(company_item, null, false);//fetchDataForCompany(company_item);
            });
        }
        //restResponse.setObject(company);
        return restResponse;
    }

    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsForDate(@PathVariable("date") String _date) {
        return this.getNewsForDateStartingWith(_date, 0);

    }

    @RequestMapping(value = "/date/{date}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsForDateStartingWith(@PathVariable("date") String _date, @PathVariable("start") int _start) {
        return this.getNewsForDateStartingWithInRange(_date, _start, 25);

    }

    @RequestMapping(value = "/date/{date}/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getNewsForDateStartingWithInRange(@PathVariable("date") String _date, @PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        RestResponse restResponse = new RestResponse();
        //CompanySearchProperties csp = new CompanySearchProperties();
        //csp.setCompanyId(_id);

        //List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<CompanyNewsItem> companyNewsItems = new ArrayList<>();

        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        //cnsp.setStockExchange(company.getStockExchange());
        //cnsp.setStockSymbol(company.getStockSymbol());
        cnsp.setSearchDate(_date);
        cnsp.setSortField("recordDate");
        cnsp.setSortOrder("DESC");
        cnsp.setStartResults(_start);
        cnsp.setNumResults(_numberOfItems);

        //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(company);
        List<CompanyNewsItem> cnis = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (cnis != null) {
            companyNewsItems.addAll(cnis);
        }
        restResponse.setObject(companyNewsItems);
        return restResponse;
    }

    @RequestMapping(value = "/userrating", method = RequestMethod.POST, produces = "application/json")
    public RestResponse saveUserRatingForNewsItems(@RequestBody NewsItemForm _newsItemForm) {
        RestResponse restResponse = new RestResponse();
        CompanyNewsSearchProperties cnsp = new CompanyNewsSearchProperties();
        cnsp.setCompanyNewsItemId(_newsItemForm.getId());
        List<CompanyNewsItem> cniList = this.companyNewsSearchRepo.searchForCompanyNews(cnsp);
        if (cniList != null && cniList.size() == 1) {
            CompanyNewsItem cni = cniList.get(0);
            cni.setUserRating(NewsItemRating.valueOf(_newsItemForm.getRating()));
            this.companyNewsSearchRepo.updateUserRatingForNewsItem(cni);
        }

        return restResponse;
    }

}
