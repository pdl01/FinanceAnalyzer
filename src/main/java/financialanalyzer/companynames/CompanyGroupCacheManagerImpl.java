/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.text.diff.StringsComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author phil
 */
@Service
public class CompanyGroupCacheManagerImpl implements CompanyGroupingCacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyGroupCacheManagerImpl.class.getName());
    @Autowired
    private CompanyRepo companySearchRepo;

    List<String> sectorNames;
    List<String> industryNames;
    boolean isBuilding;
    
    @Override
    public List<String> getIndustryNames() {
        if (this.industryNames == null) {
            this.buildCache();
        }
        return this.industryNames;
    }

    private void buildCache() {
        if (this.isBuilding) {
            return;
        }
        this.isBuilding = true;
        HashMap<String,Boolean> sectorNameMap = new HashMap<>();
        HashMap<String,Boolean> industryNameMap = new HashMap<>();
        
        String[] exchangeArray = {CompanyNameProvider.EXCHANGE_AMEX, CompanyNameProvider.EXCHANGE_NASDAQ, CompanyNameProvider.EXCHANGE_NYSE};

        
        for (String exchange : exchangeArray) {
            CompanySearchProperties csp = new CompanySearchProperties();
            csp.setStockExchange(exchange);
            int numResultsPerBatch = 200;
            boolean hasMoreResults = true;
            csp.setStartResults(0);
            csp.setNumResults(numResultsPerBatch);

            while (hasMoreResults) {
                List<Company> companies = this.companySearchRepo.searchForCompany(csp);
                if (companies != null) {
                    LOGGER.info("Processing :" + companies.size() + " companies");
                } else {
                    LOGGER.info("No companies returned");
                }
                if (companies != null) {
                    for (Company item : companies) {
                        LOGGER.info("Submitting:" + item.getStockExchange() + item.getName() + ":" + item.getStockSymbol());
                        if (item.getSectors() != null) {
                            for (String sector: item.getSectors()) {
                                sectorNameMap.put(sector, true);
                            }
                           
                        }
                        if (item.getIndustries() != null) {
                            for (String industry: item.getIndustries()) {
                                industryNameMap.put(industry,true);
                            }
                        }
                        //this.companyNewsServiceImpl.submitCompanyToDownloadQueue(item);
                    }
                }
                if (companies != null && companies.size() == numResultsPerBatch) {
                    hasMoreResults = true;
                    csp.setStartResults(csp.getStartResults() + numResultsPerBatch);
                } else {
                    hasMoreResults = false;
                }
            }

        }
        this.industryNames = new ArrayList<>();
        this.industryNames.addAll(industryNameMap.keySet());
        Collections.sort(this.industryNames);
        this.sectorNames = new ArrayList();
        this.sectorNames.addAll(sectorNameMap.keySet());
        Collections.sort(this.sectorNames);
        this.isBuilding = false;
        
    }

    @Override
    public List<String> getSectorNames() {
        if (this.sectorNames == null) {
            this.buildCache();
        }
        return this.sectorNames;
    }

}
