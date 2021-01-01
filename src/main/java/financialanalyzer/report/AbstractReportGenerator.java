package financialanalyzer.report;

import financialanalyzer.companynames.CompanySearchRepo;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author pldor
 */
public abstract class AbstractReportGenerator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractReportGenerator.class.getName());

    @Autowired
    private CompanySearchRepo companySearchRepo;
    
    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;
    
    protected String getCompanyName(String _exchange, String _symbol) {
        CompanySearchProperties csp = new CompanySearchProperties();
        csp.setStockExchange(_exchange);
        csp.setStockSymbol(_symbol);
        int numResultsPerBatch = 1;
        csp.setStartResults(0);
        csp.setNumResults(numResultsPerBatch);

        List<Company> companies = this.companySearchRepo.searchForCompany(csp);
        if (companies != null && companies.size() > 0) {
            return companies.get(0).getName();
        }
        return "";
    }

    protected String buildReportTags(List<StockHistory> _stockHistories) {
        StringBuilder sb = new StringBuilder();
        if (_stockHistories != null && _stockHistories.size() > 0) {
            for (StockHistory sh : _stockHistories) {
                sb.append(sh.getSymbol());
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    protected List<StockHistory> buildStockHistoryList(StockHistorySearchProperties _shsp) {
        List<StockHistory> stockHistories = new ArrayList<>();
        List<StockHistory> shs = this.stockHistorySearchRepo.searchForStockHistory(_shsp);
        if (shs != null) {
            for (StockHistory stockHistory : shs) {
                stockHistory.setCompanyName(this.getCompanyName(stockHistory.getExchange(), stockHistory.getSymbol()));
                stockHistories.add(stockHistory);
            }
            //stockHistories.addAll(shs);
        }

        return stockHistories;
    }

    public String getReportAudioScript(List<StockHistory> _stockHistories,String _date,String _headerText) {
       
        
        SimpleDateFormat longFormDaySDF = new SimpleDateFormat("EEEE MMMM d y");
        SimpleDateFormat dateConverterSDF = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append(_headerText);
        try {
            sb.append(longFormDaySDF.format(dateConverterSDF.parse(_date)));
        } catch (ParseException ex) {
            LOGGER.error("Unable to parse date" + _date, ex);
        }
        sb.append(" were ");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        for (StockHistory stockHistory : _stockHistories) {
            //TODO: do name lookup
            sb.append(stockHistory.getCompanyName());

            sb.append(" with a gain of ");
            sb.append(currencyFormat.format(stockHistory.getActual_gain()));
            sb.append(" from ");
            sb.append(currencyFormat.format(stockHistory.getOpen()));
            sb.append(" to ");
            sb.append(currencyFormat.format(stockHistory.getClose()));
            sb.append(", ");
        }

        return sb.toString();
    }
    
}
