/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class CompanyNewsProviderRegistry {
    @Autowired
    private List<CompanyNewsProvider> providers;

    public List<CompanyNewsProvider> getProviders() {
        return providers;
    }
    
}
