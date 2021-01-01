/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class TopLosersByPercentageGenerator extends AbstractReportGenerator implements ReportGenerator {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TopLosersByPercentageGenerator.class.getName());

    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;

    @Override
    public ReportSummary getReport(String _startDate, String _endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getReportAudioScript(String _date, int numOfItemsToInclude) {
        List<StockHistory> stockHistories = this.getReport(_date, 0, numOfItemsToInclude);
        String header = "The top losers by percentage for ";

        SimpleDateFormat longFormDaySDF = new SimpleDateFormat("EEEE MMMM d y");
        SimpleDateFormat dateConverterSDF = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append(header);

        try {
            sb.append(longFormDaySDF.format(dateConverterSDF.parse(_date)));
        } catch (ParseException ex) {
            LOGGER.error("Unable to parse date" + _date, ex);
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        sb.append(" were ");
        for (StockHistory stockHistory : stockHistories) {
            //TODO: do name lookup
            sb.append(stockHistory.getCompanyName());

            sb.append(" with a loss of ");
            sb.append(percentFormat.format(stockHistory.getPercent_gain()));
            sb.append(" from ");
            sb.append(currencyFormat.format(stockHistory.getOpen()));
            sb.append(" to ");
            sb.append(currencyFormat.format(stockHistory.getClose()));
            sb.append(", ");
        }

        return sb.toString();
    }

    @Override
    public String getId() {
        return "losers-percent";
    }

    @Override
    public List<StockHistory> getReport(String _date, int _start, int _numResults) {

        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        shsp.setSearchDate(_date);
        shsp.setStartResults(_start);
        shsp.setNumResults(_numResults);
        shsp.setSortField("percent_gain");
        shsp.setSortOrder("ASC");
        return this.buildStockHistoryList(shsp);
    }

    @Override
    public String getReportTags(String _date, int numOfItemsToInclude) {
        List<StockHistory> stockHistories = this.getReport(_date, 0, numOfItemsToInclude);
        return this.buildReportTags(stockHistories);
    }
}
