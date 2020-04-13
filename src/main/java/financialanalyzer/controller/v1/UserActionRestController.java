/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.controller.v1;

import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.systemactivity.SystemActivity;
import financialanalyzer.systemactivity.SystemActivityManager;
import financialanalyzer.systemactivity.SystemActivityRepo;
import financialanalyzer.systemactivity.SystemActivitySearchProperties;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/api/v1/useractions")
public class UserActionRestController {

    @Autowired
    private SystemActivityManager systemActivityManagerImpl;

    @Autowired
    private SystemActivityRepo systemActivitySearchRepo;

    @RequestMapping(value = "/recent-searches", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getSystemActivityRecentSearches() {
        RestResponse restResponse = new RestResponse();
        CompanySearchProperties csp = new CompanySearchProperties();
        List<SystemActivity> systemActivities = new ArrayList<>();
        SystemActivitySearchProperties shsp = new SystemActivitySearchProperties();
        shsp.setActivityType(SystemActivityManager.ACTIVITY_TYPE_STOCK_SYMBOL_SEARCH);
        shsp.setSortField("recordDate");
        shsp.setSortOrder("DESC");
        List<SystemActivity> shs = this.systemActivitySearchRepo.searchForSystemActivity(shsp);
        if (shs != null) {
            systemActivities.addAll(shs);
        }
        restResponse.setObject(systemActivities);
        return restResponse;
    }
}
