package FactoryJill;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class FactoryJill {

    private static Map<String, Blueprint> factories = new HashMap<>();

    public static <T> void factory(String alias, Class<T> clazz, Map<String, Object> attributes) throws Exception {
        factory(alias, clazz, attributes, Collections.EMPTY_MAP);
    }

    public static <T> void factory(String alias, Class<T> clazz,
                                   Map<String, Object> attributes, Map<String, String> associations) throws Exception {
        Map<String, Object> mutableAttributes = new HashMap<>(attributes);

        for (Map.Entry<String, String> entry : associations.entrySet()) {
            mutableAttributes.put(entry.getKey(), build(entry.getValue()));
        }

        Blueprint blueprint = new Blueprint(clazz, mutableAttributes);

        T newInstance = clazz.getConstructor().newInstance();
        for (Map.Entry<String, Object> attribute : mutableAttributes.entrySet()) {
            checkProperty(newInstance, attribute.getKey(), attribute.getValue(), "factory");
        }

        factories.put(alias, blueprint);
    }

    public static Object build(String factoryName) throws Exception {
        return build(factoryName, new HashMap<>());
    }

    public static <T> T build(String factoryName, Map<String, Object> overrides) throws Exception {
        Blueprint<T> blueprint = factories.get(factoryName);
        Class<T> clazz = blueprint.getClazz();
        Constructor<?> constructor = clazz.getConstructor();
        T newInstance = (T) constructor.newInstance();

        for (Map.Entry<String, Object> entry : blueprint.getAttributes().entrySet()) {
            setProperty(newInstance, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Object> entry : overrides.entrySet()) {
            setProperty(newInstance, entry.getKey(), entry.getValue());
        }

        return newInstance;
    }

    private static <T> void setProperty(T newInstance, String property, Object potentialValue) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object value;

        if (potentialValue instanceof Function) {
            value = ((Function) potentialValue).apply(newInstance);
        } else {
            value = potentialValue;
        }

        checkProperty(newInstance, property, value, "override");
        BeanUtils.setProperty(newInstance, property, value);

        if (BeanUtils.get(newInstance, property) != value) {
            throw new IllegalArgumentException();
        }
    }

    private static <T> void checkProperty(T newInstance, String property, Object value, String configurationType) throws IllegalAccessException, InvocationTargetException {
        try {
            BeanUtils.getProperty(newInstance, property);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new IllegalArgumentException(String.format("Failed to set %s to %s on class %s, check your %s configuration",
                    property, value, newInstance.getClass().getSimpleName(), configurationType), noSuchMethodException);
        }
    }

    private static <T> Method getMethodByName(T newInstance, String setter) throws NoSuchMethodException {
        Method[] methods = newInstance.getClass().getMethods();
        Method matchingMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(setter)) {
                matchingMethod = method;
            }
        }
        if (matchingMethod != null) {
            return matchingMethod;
        } else {
            throw new NoSuchMethodException();
        }
    }
}
