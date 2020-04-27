/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynames;

import financialanalyzer.objects.Company;
import financialanalyzer.stockhistory.StockHistory;
import java.util.Date;
import java.util.List;

/**
 *
 * @author pldor
 */
public interface CompanyNameProvider {

    public static final String EXCHANGE_NASDAQ = "nasdaq";
    public static final String EXCHANGE_AMEX = "amex";
    public static final String EXCHANGE_NYSE = "nyse";

    public List<Company> getAllCompanies();

    public List<Company> getCompaniesBeginningWithLetter(String _letter);
    
    public String getIdentifier();
    

    //List<StockHistory> getStockHistoryForCompany(String _symbol);

    //List<StockHistory> getStockHistoryForCompanyForDay(String _symbol, Date _date);
}
