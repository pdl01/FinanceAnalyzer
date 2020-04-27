/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.config.AppConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsProviderRegistry {
    private CompanyNewsProvider preferredProvider;
    
    @Autowired
    private List<CompanyNewsProvider> providers;

    @Autowired
    private AppConfig appConfig;
    
    
    public List<CompanyNewsProvider> getProviders() {
        return providers;
    }
    
    public CompanyNewsProvider getPreferredProvider(){
        if (preferredProvider == null) {
            if (providers != null) {
                for (int i=0;i<this.providers.size();i++) {
                    if (this.providers.get(i).getIdentifier().equalsIgnoreCase(this.appConfig.getPreferredCompanyNewsProvider())) {
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
    
    public CompanyNewsProvider getRandomProvider() {
        int index = (int) (Math.random() * providers.size());
        return this.providers.get(index);
    }
    
    
}
