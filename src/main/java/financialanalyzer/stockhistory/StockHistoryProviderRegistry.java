/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockhistory;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class StockHistoryProviderRegistry {

    @Autowired
    private List<StockHistoryProvider> providers;

    public List<StockHistoryProvider> getProviders() {
        return providers;
    }
    public StockHistoryProvider getPreferred(){
        if (this.providers != null && this.providers.size()>0) {
            return this.providers.get(0);
        }
        return null;
    }
}
