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

    public StatusCode put(String key, String value) {
        if (mConfigurations.containsKey(key)) {
            return StatusCode.DUPLICATED_IDENTIFIER;
        } else {
            mConfigurations.put(key, value);
            return StatusCode.SUCCESS;
        }
    }

    public String get(String key) {
        if (!mConfigurations.containsKey(key)) {
            return null;
        } else {
            return mConfigurations.get(key);
        }
    }

    public Set<String> keySet() {
        return mConfigurations.keySet();
    }

    public boolean containsKey(String key) {
        return mConfigurations.containsKey(key);
    }
}
