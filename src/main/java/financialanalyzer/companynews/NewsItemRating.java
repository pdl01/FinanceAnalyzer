/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.companynews;

/**
 *
 * @author phil
 */
public enum NewsItemRating {
    NONE("O"),
    UNRELATED("U"),
    POSITIVE("P"),
    NEGATIVE("N");
    
    
    private final String shortCode;

    NewsItemRating(String code) {
        this.shortCode = code;
    }

    public String getCode() {
        return this.shortCode;
    }
}
