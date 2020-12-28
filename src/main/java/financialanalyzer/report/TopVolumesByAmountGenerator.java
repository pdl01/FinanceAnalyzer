package financialanalyzer.report;

import financialanalyzer.companynames.CompanySearchRepo;
import financialanalyzer.objects.Company;
import financialanalyzer.objects.CompanySearchProperties;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockhistory.StockHistoryReportSearchRepo;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class TopVolumesByAmountGenerator implements ReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopVolumesByAmountGenerator.class.getName());

    @Autowired
    private StockHistoryReportSearchRepo stockHistoryReportSearchRepo;
    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;
    @Autowired
    private CompanySearchRepo companySearchRepo;

    @Override
    public ReportSummary getReport(String _startDate, String _endDate) {
        SearchRequest searchRequest = new SearchRequest(StockHistoryReportSearchRepo.STOCK_HISTORY_INDEX);

        this.stockHistoryReportSearchRepo.searchForReport(searchRequest);
        ReportSummary summary = null;
        return summary;
    }

    @Override
    public String getReportAudioScript(String _date, int numOfItemsToInclude) {
        List<StockHistory> stockHistories = this.getReport(_date, 0, numOfItemsToInclude);
        String header = "The top volumes by amount for ";

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
        //NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        //NumberFormat percentFormat = NumberFormat.getPercentInstance();
        NumberFormat largeNumberFormat = NumberFormat.getNumberInstance();
        for (StockHistory stockHistory : stockHistories) {
            //TODO: do name lookup
            sb.append(stockHistory.getCompanyName());

            sb.append(" with ");
            sb.append(largeNumberFormat.format(stockHistory.getVolume()));
            sb.append(", ");
        }

        return sb.toString();

    }

    @Override
    public String getId() {
        return "volumes";
    }

    @Override
    public List<StockHistory> getReport(String _date, int _start, int _numResults) {
        LOGGER.debug("Inside getReport");
        List<StockHistory> stockHistories = new ArrayList<>();

        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        shsp.setSearchDate(_date);
        shsp.setStartResults(_start);
        shsp.setNumResults(_numResults);
        shsp.setSortField("volume");
        shsp.setSortOrder("DESC");
        List<StockHistory> shs = this.stockHistorySearchRepo.searchForStockHistory(shsp);
        if (shs != null) {
            for (StockHistory stockHistory : shs) {
                stockHistory.setCompanyName(this.getCompanyName(stockHistory.getExchange(), stockHistory.getSymbol()));
                stockHistories.add(stockHistory);
            }
            //stockHistories.addAll(shs);
        }

        return stockHistories;
    }

    private String getCompanyName(String _exchange, String _symbol) {
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
}
