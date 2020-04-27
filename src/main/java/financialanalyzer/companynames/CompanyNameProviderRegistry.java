/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import financialanalyzer.companynews.CompanyNewsProvider;
import financialanalyzer.config.AppConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class CompanyNameProviderRegistry {

    private CompanyNameProvider preferredProvider;
    
    @Autowired
    private List<CompanyNameProvider> providers;

    @Autowired
    private AppConfig appConfig;    
    
    public List<CompanyNameProvider> getProviders() {
        return providers;
    }
    public CompanyNameProvider getPreferredProvider(){
        if (preferredProvider == null) {
            if (providers != null) {
                for (int i=0;i<this.providers.size();i++) {
                    if (this.providers.get(i).getIdentifier().equalsIgnoreCase(this.appConfig.getPreferredCompanyNameProvider())) {
                        this.preferredProvider = this.providers.get(i);
                    }
                }
                if (this.preferredProvider == null) {
                    this.preferredProvider = this.getRandomProvider();
                }
            }
        }
        return this.preferredProvider;
    }
    
    public CompanyNameProvider getRandomProvider() {
        //TODO:Random logic
        int index = (int) (Math.random() * providers.size());
        return this.providers.get(index);
    }    
    
}
