/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.objects.AbstractSearchProperties;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pldor
 */
public class CompanyNewsSearchProperties extends AbstractSearchProperties {

    protected String companyNewsItemId;
    protected String subject;
    protected List<NewsItemRating> includedSystemRatings;
    protected List<NewsItemRating> includedUserRatings;
 
    public String getCompanyNewsItemId() {
        return companyNewsItemId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCompanyNewsItemId(String companyNewsItemId) {
        this.companyNewsItemId = companyNewsItemId;
    }

    public List<NewsItemRating> getIncludedSystemRatings() {
        return includedSystemRatings;
    }

    public void setIncludedSystemRating(List<NewsItemRating> includedSystemRatings) {
        this.includedSystemRatings = includedSystemRatings;
    }

    public List<NewsItemRating> getIncludedUserRatings() {
        return includedUserRatings;
    }

    public void setIncludedUserRatings(List<NewsItemRating> includedUserRatings) {
        this.includedUserRatings = includedUserRatings;
    }
    public void addIncludedUserRating(NewsItemRating _item) {
        if (this.includedUserRatings == null) {
            this.includedUserRatings = new ArrayList<>();
        }
        this.includedUserRatings.add(_item);
    }
    public void addIncludedSystemRating(NewsItemRating _item) {
        if (this.includedSystemRatings == null) {
            this.includedSystemRatings = new ArrayList<>();
        }
        this.includedSystemRatings.add(_item);        
    }
    
    
    

}
