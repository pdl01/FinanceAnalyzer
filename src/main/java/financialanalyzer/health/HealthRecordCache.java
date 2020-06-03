/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.health;

import java.util.*;
import org.springframework.stereotype.Component;

/**
 *
 * @author phil
 */
@Component
public class HealthRecordCache {
    private Map<String,HealthRecord> healthRecordCache = null;
    
    private void initCache() {
        if (this.healthRecordCache == null) {
            this.healthRecordCache = new HashMap<>();
        }
    }
    public void put(String key,HealthRecord _hr) {
        this.initCache();
        this.healthRecordCache.put(key, _hr);
    }
    public HealthRecord get(String key) {
        this.initCache();
        return this.healthRecordCache.get(key);
    }
    public void clearCache() {
        this.healthRecordCache = null;
    }
    public List<String> getKeys() {
        if (this.healthRecordCache == null) {
            return null;
        }
        ArrayList<String> keys = new ArrayList<>();
        keys.addAll(this.healthRecordCache.keySet());
        return keys;
    }
}
