/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import java.util.List;

/**
 *
 * @author phil
 */
public interface CompanyGroupingCacheManager {
    public List<String> getIndustryNames();
    public List<String> getSectorNames();
}
