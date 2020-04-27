/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.controller.v1;

import financialanalyzer.companynews.CompanyNewsItem;
import financialanalyzer.companynews.CompanyNewsService;
import financialanalyzer.companynames.AllStockNamesDownloadDriver;
import financialanalyzer.stockhistory.StockHistoryDownloadDriver;
import financialanalyzer.stockhistory.StockHistoryDownloadService;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import financialanalyzer.companynames.CompanyRepo;
import financialanalyzer.companynews.CompanyNewsDownloadDriver;
import financialanalyzer.companynews.CompanyNewsRepo;
import financialanalyzer.companynews.CompanyNewsSearchProperties;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.systemactivity.SystemActivity;
import financialanalyzer.systemactivity.SystemActivityManager;
import financialanalyzer.systemactivity.SystemActivityRepo;
import financialanalyzer.systemactivity.SystemActivitySearchProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author pldor
 */
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyRestController {

    private static final Logger logger = Logger.getLogger(CompanyRestController.class.getName());
    @Autowired
    private CompanyRepo companySearchRepo;

    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;

    @Autowired
    private AllStockNamesDownloadDriver allStockNamesDownloadDriver;

    @Autowired
    private StockHistoryDownloadDriver stockHistoryDownloadDriver;

    @Autowired
    private StockHistoryDownloadService stockHistoryDownloadServiceImpl;

    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

    @Autowired
    private SystemActivityRepo systemActivitySearchRepo;


    
    @RequestMapping(value = "/symbol/{symbol}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompaniesBySymbol(@PathVariable("symbol") String symbol) {
        RestResponse restResponse = new RestResponse();
        this.systemActivityManagerImpl.saveSystemActivity(symbol, null, SystemActivityManager.ACTIVITY_TYPE_STOCK_SYMBOL_SEARCH, "Search Performed");
        CompanySearchProperties csp = new CompanySearchProperties();

        csp.setStockSymbol(symbol);
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        restResponse.setObject(companies);
        return restResponse;
    }

    @RequestMapping(value = "/company/{id}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompanyById(@PathVariable("id") String _id) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setCompanyId(_id);
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        restResponse.setObject(companies);
        return restResponse;
    }

    @RequestMapping(value = "/exchange/{exchange}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompaniesByExchange(@PathVariable("exchange") String exchange) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setStockExchange(exchange);
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        restResponse.setObject(companies);
        return restResponse;
    }

    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getCompaniesByName(@PathVariable("name") String name) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setCompanyName(name);
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        restResponse.setObject(companies);
        return restResponse;
    }

    @RequestMapping(value = "/createDraft", method = RequestMethod.POST, produces = "application/json")
    public RestResponse saveCompany(@RequestBody Company _company) {
        RestResponse restResponse = new RestResponse();
        Company company = this.companySearchRepo.submit(_company);
        restResponse.setObject(company);
        return restResponse;
    }

    @RequestMapping(value = "/companies/fetchLatestData", method = RequestMethod.POST, produces = "application/json")
    public RestResponse triggerCompanyNameDownload() {
        RestResponse restResponse = new RestResponse();
        this.allStockNamesDownloadDriver.fetchLatestData();
        //restResponse.setObject(company);
        return restResponse;
    }

    @RequestMapping(value = "/stockhistory/fetchLatestData", method = RequestMethod.POST, produces = "application/json")
    public RestResponse triggerStockHistoryDownload() {
        RestResponse restResponse = new RestResponse();
        this.stockHistoryDownloadDriver.fetchLatestData();
        //restResponse.setObject(company);
        return restResponse;
    }



    @RequestMapping(value = "/symbol/{symbol}/stock/fetch", method = RequestMethod.POST, produces = "application/json")
    public RestResponse fetchStockInformation(@PathVariable("symbol") String symbol) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setStockSymbol(symbol);
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        if (companies != null) {
            companies.forEach(company_item -> {
                this.stockHistoryDownloadServiceImpl.queueCompanyForFetch(company_item, null, false);//fetchDataForCompany(company_item);
            });
        }
        //restResponse.setObject(company);
        return restResponse;
    }

    @RequestMapping(value = "/company/{id}/stock", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getStockInformationForCompany(@PathVariable("id") String _id) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setCompanyId(_id);;
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<StockHistory> stockhistories = new ArrayList<>();
        if (companies != null) {
            for (Company company : companies) {
                StockHistorySearchProperties shsp = new StockHistorySearchProperties();
                shsp.setStockExchange(company.getStockExchange());
                shsp.setStockSymbol(company.getStockSymbol());
                shsp.setSortField("recordDate");
                shsp.setSortOrder("DESC");
                List<StockHistory> shs = this.stockHistorySearchRepo.searchForStockHistory(shsp);
                if (shs != null) {
                    stockhistories.addAll(shs);
                }
            }
        }
        //restResponse.setObject(company);
        restResponse.setObject(stockhistories);

        return restResponse;
    }

    @RequestMapping(value = "/company/{id}/systemActivity", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getSystemActivityForCompany(@PathVariable("id") String _id) {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setCompanyId(_id);;
        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        List<SystemActivity> systemActivities = new ArrayList<>();
        if (companies != null) {
            for (Company company : companies) {
                SystemActivitySearchProperties shsp = new SystemActivitySearchProperties();
                shsp.setStockExchange(company.getStockExchange());
                shsp.setStockSymbol(company.getStockSymbol());
                shsp.setSortField("recordDate");
                shsp.setSortOrder("DESC");
                List<SystemActivity> shs = this.systemActivitySearchRepo.searchForSystemActivity(shsp);
                if (shs != null) {
                    systemActivities.addAll(shs);
                }
            }
        }
        restResponse.setObject(systemActivities);
        return restResponse;
    }



}
