package financialanalyzer.report;

import financialanalyzer.companynames.CompanySearchRepo;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import financialanalyzer.stockperformance.StockPerformanceRepo;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class TopGainersByAmountGenerator extends AbstractReportGenerator implements ReportGenerator {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TopGainersByAmountGenerator.class.getName());



    @Autowired
    private StockPerformanceRepo stockPerformanceSearchRepo;

    @Override
    public ReportSummary getReport(String _startDate, String _endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getReportAudioScript(String _date, int numOfItemsToInclude) {
        List<StockHistory> stockHistories = this.getReport(_date, 0, numOfItemsToInclude);
        String header = "The top gainers by amount for ";
        return this.getReportAudioScript(stockHistories, _date, header);
        
        /*
        SimpleDateFormat longFormDaySDF = new SimpleDateFormat("EEEE MMMM d y");
        SimpleDateFormat dateConverterSDF = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append(header);
        try {
            sb.append(longFormDaySDF.format(dateConverterSDF.parse(_date)));
        } catch (ParseException ex) {
            LOGGER.error("Unable to parse date" + _date, ex);
        }
        sb.append(" were ");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        for (StockHistory stockHistory : stockHistories) {
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
        */
    }



    @Override
    public List<StockHistory> getReport(String _date, int _start, int _numResults) {

        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        shsp.setSearchDate(_date);
        shsp.setStartResults(_start);
        shsp.setNumResults(_numResults);
        shsp.setSortField("actual_gain");
        shsp.setSortOrder("DESC");
        return this.buildStockHistoryList(shsp);

    }

    @Override
    public String getId() {
        return "gainers-amount";
    }

    @Override
    public String getReportTags(String _date, int numOfItemsToInclude) {
        List<StockHistory> stockHistories = this.getReport(_date, 0, numOfItemsToInclude);
        return this.buildReportTags(stockHistories);
    }
}
