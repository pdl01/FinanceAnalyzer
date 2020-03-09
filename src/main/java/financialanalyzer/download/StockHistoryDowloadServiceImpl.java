/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.download;

import financialanalyzer.objects.Company;
import financialanalyzer.objects.StockHistory;
import financialanalyzer.respository.StockHistorySearchRepo;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class StockHistoryDowloadServiceImpl implements StockHistoryDownloadService {

    @Autowired
    private CompanyProvider advfnAMEXCompanyProvider;

    @Autowired
    private CompanyProvider advfnNYSECompanyProvider;

    @Autowired
    private CompanyProvider advfnNasDaqCompanyProvider;

    @Autowired
    private StockHistorySearchRepo stockHistorySearchRepoImpl;
    
    @Override
    public List<StockHistory> fetchDataForCompany(Company company) {
        return this.fetchDataForCompany(company, null);
    }

    @Override
    public List<StockHistory> fetchDataForCompany(Company company, Date _date) {
        if (company == null) {
            return null;
        }
        List<StockHistory> shs = null;
        if (company.getStockExchange().equalsIgnoreCase(CompanyProvider.EXCHANGE_NASDAQ)) {
            shs = this.advfnNasDaqCompanyProvider.getStockHistoryForCompany(company.getStockSymbol());
        } else if (company.getStockExchange().equalsIgnoreCase(CompanyProvider.EXCHANGE_NYSE)) {
            shs = this.advfnNYSECompanyProvider.getStockHistoryForCompany(company.getStockSymbol());
        } else if (company.getStockExchange().equalsIgnoreCase(CompanyProvider.EXCHANGE_AMEX)) {
            shs = this.advfnAMEXCompanyProvider.getStockHistoryForCompany(company.getStockSymbol());
        }
        if (shs != null) {
            for (StockHistory item: shs) {
                this.stockHistorySearchRepoImpl.submit(item);
            }
            /*
            shs.forEach(item -> {
                this.stockHistorySearchRepoImpl.submit(item);

            });
            */
        }
        return shs;
    }

}