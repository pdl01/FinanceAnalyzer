/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.objects.AbstractSearchProperties;

/**
 *
 * @author pldor
 */
public class CompanyNewsSearchProperties extends AbstractSearchProperties {

    protected String companyNewsItemId;
    protected NewsItemRating systemRating;
    protected NewsItemRating userRating;
 
    public String getCompanyNewsItemId() {
        return companyNewsItemId;
    }

    public void setCompanyNewsItemId(String companyNewsItemId) {
        this.companyNewsItemId = companyNewsItemId;
    }

    public NewsItemRating getSystemRating() {
        return systemRating;
    }

    public void setSystemRating(NewsItemRating systemRating) {
        this.systemRating = systemRating;
    }

    public NewsItemRating getUserRating() {
        return userRating;
    }

    public void setUserRating(NewsItemRating userRating) {
        this.userRating = userRating;
    }

    

}
