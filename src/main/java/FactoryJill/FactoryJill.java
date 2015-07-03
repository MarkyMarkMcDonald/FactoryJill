package FactoryJill;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class FactoryJill {

    private static Map<String, Blueprint> factories = new HashMap<>();

    public static <T> void factory(String alias, Class<T> clazz, Map<String, Object> attributes, Map<String, String> associations) throws Exception {
        Map<String, Object> derivedAttributes = flattenAssociations(attributes, associations);

        Blueprint blueprint = new Blueprint(clazz, derivedAttributes);

        T newInstance = clazz.getConstructor().newInstance();
        for (Map.Entry<String, Object> attribute : derivedAttributes.entrySet()) {
            if (propertyMissing(newInstance, attribute.getKey())) {
                throw new IllegalArgumentException(String.format("Failed to define factory \"%s\", could not set \"%s\" to \"%s\" on class \"%s\".",
                        alias, attribute.getKey(), attribute.getValue(), newInstance.getClass().getSimpleName()));
            }
        }

        factories.put(alias, blueprint);
    }

    public static <T> T build(String factoryName, Map<String, Object> overrides) throws Exception {
        Blueprint<T> blueprint = factories.get(factoryName);

        if (blueprint == null) {
            throw new IllegalArgumentException(String.format("No \"%s\" factory has been defined. Defined factories: %s.", factoryName, getDefinedFactoryNames()));
        }

        Class<T> clazz = blueprint.getClazz();
        Constructor<?> constructor = clazz.getConstructor();
        T newInstance = (T) constructor.newInstance();

        for (Map.Entry<String, Object> defaultField : blueprint.getAttributes().entrySet()) {
            if (propertyMissing(newInstance, defaultField.getKey())) {
                throw new IllegalArgumentException(getDefaultFieldErrorMessage(factoryName, newInstance, defaultField));
            }
            BeanUtils.setProperty(newInstance, defaultField.getKey(), defaultField.getValue());
        }

        for (Map.Entry<String, Object> override : overrides.entrySet()) {
            if (propertyMissing(newInstance, override.getKey())) {
                throw new IllegalArgumentException(getOverrideErrorMessage(factoryName, newInstance, override));
            }
            BeanUtils.setProperty(newInstance, override.getKey(), override.getValue());
        }

        return newInstance;
    }

    public static <T> List<T> buildMultiple(String alias, Integer count) throws Exception {
        List<T> things = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            things.add(build(alias));
        }
        return things;
    }

    /*
     Method clones with defaulted params
     */

    public static <T> void factory(String alias, Class<T> clazz, Map<String, Object> attributes) throws Exception {
        factory(alias, clazz, attributes, Collections.EMPTY_MAP);
    }

    public static <T> T build(String factoryName) throws Exception {
        return build(factoryName, new HashMap<>());
    }

    public static <T> List<T> buildMultiple(String alias) throws Exception {
        return buildMultiple(alias, 10);
    }

    private static Map<String, Object> flattenAssociations(Map<String, Object> attributes, Map<String, String> associations) throws Exception {
        Map<String, Object> mutableAttributes = new HashMap<>(attributes);
        for (Map.Entry<String, String> entry : associations.entrySet()) {
            mutableAttributes.put(entry.getKey(), build(entry.getValue()));
        }
        return mutableAttributes;
    }

    private static <T> String getDefaultFieldErrorMessage(String factoryName, T newInstance, Map.Entry<String, Object> entry) {
        return String.format("Failed to build \"%s\", could not set \"%s\" to \"%s\" on class \"%s\".",
                            factoryName, entry.getKey(), entry.getValue(), newInstance.getClass().getSimpleName());
    }

    private static <T> String getOverrideErrorMessage(String factoryName, T newInstance, Map.Entry<String, Object> entry) {
        return String.format("Failed to build \"%s\", could not override \"%s\" to \"%s\" on class \"%s\".",
                            factoryName, entry.getKey(), entry.getValue(), newInstance.getClass().getSimpleName());
    }

    private static List<String> getDefinedFactoryNames() {
        return factories.keySet().stream().sorted().collect(Collectors.toList());
    }

    private static <T> boolean propertyMissing(T newInstance, String property) throws IllegalAccessException, InvocationTargetException {
        try {
            BeanUtils.getProperty(newInstance, property);
            return false;
        } catch (NoSuchMethodException noSuchMethodException) {
            return true;
        }
    }
}
