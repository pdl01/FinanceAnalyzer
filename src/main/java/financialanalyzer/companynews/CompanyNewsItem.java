/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import java.util.Date;
import java.util.List;

/**
 *
 * @author pldor
 */
public class CompanyNewsItem implements Cloneable {

    
    private String id;
    private String url;
    private String subject;
    private String body;
    private String exchange;
    private String symbol;
    private Date publishedDate;
    private String publishedDateAsString;
    private String recordDateAsString;
    private Date recordDate;
    private NewsItemRating userRating;
    private String systemRatingVersion;
    private NewsItemRating systemRating;
    private List<String> sectors;
    private List<String> industries;
    private String enhancementVersion;

    public String getId() {
        return id;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getPublishedDateAsString() {
        return publishedDateAsString;
    }

    public void setPublishedDateAsString(String publishedDateAsString) {
        this.publishedDateAsString = publishedDateAsString;
    }

    public String getRecordDateAsString() {
        return recordDateAsString;
    }

    public void setRecordDateAsString(String recordDateAsString) {
        this.recordDateAsString = recordDateAsString;
    }

    public NewsItemRating getUserRating() {
        return userRating;
    }

    public void setUserRating(NewsItemRating userRating) {
        this.userRating = userRating;
    }

    public NewsItemRating getSystemRating() {
        return systemRating;
    }

    public void setSystemRating(NewsItemRating systemRating) {
        this.systemRating = systemRating;
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

    public String getSystemRatingVersion() {
        return systemRatingVersion;
    }

    public void setSystemRatingVersion(String systemRatingVersion) {
        this.systemRatingVersion = systemRatingVersion;
    }

    public String getEnhancementVersion() {
        return enhancementVersion;
    }

    public void setEnhancementVersion(String enhancementVersion) {
        this.enhancementVersion = enhancementVersion;
    }



}
