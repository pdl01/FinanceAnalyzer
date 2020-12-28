
package financialanalyzer.stockhistory;

import java.util.Date;
import java.util.List;

/**
 *
 * @author pldor
 */
public class StockHistory {

    private Date recordDate;
    private String recordDateAsString;
    private String symbol;
    private String exchange;
    
    private float open;
    private float close;
    private float high;
    private float low;
    private float percent_gain;
    private float actual_gain;
    private int volume;
    private String enhancementVersion;
    private List<String> sectors;
    private List<String> industries;    
    private String companyName;
    
    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRecordDateAsString() {
        return recordDateAsString;
    }

    public void setRecordDateAsString(String recordDateAsString) {
        this.recordDateAsString = recordDateAsString;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getPercent_gain() {
        return percent_gain;
    }

    public void setPercent_gain(float percent_gain) {
        this.percent_gain = percent_gain;
    }

    public float getActual_gain() {
        return actual_gain;
    }

    public void setActual_gain(float actual_gain) {
        this.actual_gain = actual_gain;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public String getEnhancementVersion() {
        return enhancementVersion;
    }

    public void setEnhancementVersion(String enhancementVersion) {
        this.enhancementVersion = enhancementVersion;
    }

    public List<String> getSectors() {
        return sectors;
    }

    public void setSectors(List<String> sectors) {
        this.sectors = sectors;
    }

    public List<String> getIndustries() {
        return industries;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
}
