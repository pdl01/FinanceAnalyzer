/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import financialanalyzer.objects.Company;
import java.util.List;

/**
 *
 * @author pldor
 */
public interface CompanyNewsService {

    public List<CompanyNewsItem> fetchCompanyNewsItems(Company _company, int _numberOfArticlesPerProvider);

    public void submitCompanyToDownloadQueue(Company _company);

    public void submitCompanyToSentimentAnalysisQueue(Company _company);

    public void updateUserRating(String _id, NewsItemRating _nir);

    public void updateSystemRating(String _id, NewsItemRating _nir);

    public List<Company> getCompaniesThatHaveNoNewsItems();

    public List<Company> getCompaniesThatHaveNewsItemsWithNoSystemRating();

    public List<CompanyNewsItem> getNewsItemsWithNoSystemRatingForCompany(Company _company);
    
    public CompanyNewsItem buildCompanyNewsItemFromURL(String _url);
}
