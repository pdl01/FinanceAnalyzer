/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

import java.util.Date;

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
    private String sentiment;

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

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
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

}
