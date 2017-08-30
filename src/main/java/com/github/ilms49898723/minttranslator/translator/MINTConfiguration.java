package com.github.ilms49898723.minttranslator.translator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * MINT Configuration.
 */
public class MINTConfiguration {
    private Map<String, String> mConfigurations;

    public MINTConfiguration() {
        mConfigurations = new HashMap<>();
    }

    public StatusCode copy(MINTConfiguration configuration) {
        mConfigurations.putAll(configuration.mConfigurations);
        return StatusCode.SUCCESS;
    }

    public StatusCode put(String key, String value) {
        if (mConfigurations.containsKey(key)) {
            return StatusCode.FAIL;
        } else {
            mConfigurations.put(key, value);
            return StatusCode.SUCCESS;
        }
    }

    public String get(String key) {
        return mConfigurations.getOrDefault(key, null);
    }

    public int getInt(String key) {
        String value = mConfigurations.getOrDefault(key, "");
        return Integer.parseInt(value);
    }

    public double getDouble(String key) {
        String value = mConfigurations.getOrDefault(key, "");
        return Double.parseDouble(value);
    }

    public Set<String> keySet() {
        return mConfigurations.keySet();
    }

    public boolean containsKey(String key) {
        return mConfigurations.containsKey(key);
    }
}
