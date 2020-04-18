/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import financialanalyzer.companynews.CompanyNewsProvider;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class CompanyNameProviderRegistry {

    @Autowired
    private List<CompanyNameProvider> providers;

    public List<CompanyNameProvider> getProviders() {
        return providers;
    }
}
