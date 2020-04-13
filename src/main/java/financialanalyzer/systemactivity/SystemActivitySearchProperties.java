/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.systemactivity;

import financialanalyzer.objects.AbstractSearchProperties;

/**
 *
 * @author pldor
 */
public class SystemActivitySearchProperties extends AbstractSearchProperties {

 
    private String activityType;

    

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    
    
}
