/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.health;

import financialanalyzer.objects.Company;
import java.util.List;

/**
 *
 * @author phil
 */
public interface HealthService {

    
    public HealthRecord generateDailyHealthRecord();
    
    public HealthRecord generateHealthRecord(boolean _reProcessWhereAvailable);

    public List<Company> generateListOfCompaniesWithoutNewsItemsInPastXDays(int _numOfDays);

    public List<Company> generateListOfCompaniesWithoutStockHistoriesInPastXDays(int _numOfDays);
    
    public List<String> getAvailableHealthRecords();
    
    public HealthRecord getHealthRecord(String _key);
    
}
