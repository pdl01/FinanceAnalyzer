/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.controller.v1;

import financialanalyzer.health.HealthRecord;
import financialanalyzer.health.HealthService;
import financialanalyzer.objects.NewsItemForm;
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
@RequestMapping("/api/v1/systemhealth")
public class HealthController {

    @Autowired
    private HealthService healthServiceImpl;

    @RequestMapping(value = "/recordlist", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getAvailableHealthRecords() {

        RestResponse restResponse = new RestResponse();
        restResponse.setObject(this.healthServiceImpl.getAvailableHealthRecords());
        return restResponse;
    }

    @RequestMapping(value = "/item/{key}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getHealthRecord(@PathVariable("key") String _key) {
        HealthRecord hr = this.healthServiceImpl.getHealthRecord(_key);
        RestResponse restResponse = new RestResponse();
        restResponse.setObject(hr);
        return restResponse;
    }

    @RequestMapping(value = "/latest", method = RequestMethod.POST, produces = "application/json")
    public RestResponse getLatestHealth() {
        this.healthServiceImpl.generateHealthRecord(false);
        RestResponse restResponse = new RestResponse();
        return restResponse;
    }

    @RequestMapping(value = "/latest/rebuild", method = RequestMethod.POST, produces = "application/json")
    public RestResponse getLatestHealthAndRebuild() {
        this.healthServiceImpl.generateHealthRecord(true);
        RestResponse restResponse = new RestResponse();
        return restResponse;
    }

}
