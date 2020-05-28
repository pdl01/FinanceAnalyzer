/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.systemactivity.SystemActivity;
import financialanalyzer.systemactivity.SystemActivitySearchProperties;
import java.util.List;

/**
 *
 * @author pldor
 */
public interface CompanyNewsRepo {

    public CompanyNewsItem submit(CompanyNewsItem _item);

    public boolean delete(CompanyNewsItem _item);

    public List<CompanyNewsItem> searchForCompanyNews(CompanyNewsSearchProperties _sp);
    
    public long getNumberOfNewsItemsForCompany(CompanyNewsSearchProperties _sp);
    
    public boolean updateUserRatingForNewsItem(CompanyNewsItem _item);
    public boolean updateSystemRatingForNewsItems(CompanyNewsItem _item);
    
}
