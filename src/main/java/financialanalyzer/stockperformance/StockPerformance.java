/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.stockperformance;

import java.util.Date;
import java.util.List;

/**
 *
 * @author phil
 */
public class StockPerformance {

    private String id;
    private Date recordDate;
    private String recordDateAsString;
    private String symbol;
    private String exchange;
    private List<String> sectors;
    private List<String> industries;
    private float threedayperf;
    private float sevendayperf;
    private float thirtydayperf;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordDateAsString() {
        return recordDateAsString;
    }

    public void setRecordDateAsString(String recordDateAsString) {
        this.recordDateAsString = recordDateAsString;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
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

    public float getThreedayperf() {
        return threedayperf;
    }

    public void setThreedayperf(float threedayperf) {
        this.threedayperf = threedayperf;
    }

    public float getSevendayperf() {
        return sevendayperf;
    }

    public void setSevendayperf(float sevendayperf) {
        this.sevendayperf = sevendayperf;
    }

    public float getThirtydayperf() {
        return thirtydayperf;
    }

    public void setThirtydayperf(float thirtydayperf) {
        this.thirtydayperf = thirtydayperf;
    }


    
}
