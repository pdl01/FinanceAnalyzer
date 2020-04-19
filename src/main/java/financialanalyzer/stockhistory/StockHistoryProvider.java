package financialanalyzer.stockhistory;

import java.util.Date;
import java.util.List;

/**
 *
 * @author phil
 */
public interface StockHistoryProvider {

    List<StockHistory> getStockHistoryForCompany(String _exchange,String _symbol);

    List<StockHistory> getStockHistoryForCompanyForDay(String _exchange,String _symbol, Date _date);
}
