package financialanalyzer.stockhistory;

import java.util.Date;
import java.util.List;

/**
 *
 * @author phil
 */
public interface StockHistoryProvider {

    public List<StockHistory> getStockHistoryForCompany(String _exchange,String _symbol);

    public List<StockHistory> getStockHistoryForCompanyForDay(String _exchange,String _symbol, Date _date);
    
    public String getIdentifier();
}
