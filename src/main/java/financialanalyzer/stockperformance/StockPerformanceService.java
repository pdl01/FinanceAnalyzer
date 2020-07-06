/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import financialanalyzer.objects.Company;
import java.util.Date;

/**
 *
 * @author phil
 */
public interface StockPerformanceService {
        public void queueCompanyForBuild(Company company);
        public void buildStockPerformanceRecordForCompany(Company company);
        

}
