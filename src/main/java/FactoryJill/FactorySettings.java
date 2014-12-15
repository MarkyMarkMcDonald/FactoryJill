package FactoryJill;

import java.util.HashMap;
import java.util.Map;

public class FactorySettings {

    private Map<String, Object> overrides = new HashMap<>();

    public void override(String property, Object value) {
        overrides.put(property, value);
    }

    public void association(String property, String factory) {

    }

    public void association(String property, String factory, Map<String, Object> overrides) {

    }
}
