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

    public String getCompanyNewsItemId() {
        return companyNewsItemId;
    }

    public void setCompanyNewsItemId(String companyNewsItemId) {
        this.companyNewsItemId = companyNewsItemId;
    }

}
