/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import financialanalyzer.stockhistory.*;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import java.util.List;

/**
 *
 * @author pldor
 */
public interface StockPerformanceRepo {

    public StockPerformance submit(StockPerformance _item);

    public boolean delete(StockPerformance _item);

    public List<StockPerformance> searchForStockPerformance(StockPerformanceSearchProperties _shsp);

    public long searchForStockPerformanceCount(StockPerformanceSearchProperties _shsp);

}
