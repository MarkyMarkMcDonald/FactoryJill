package FactoryJill;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FactoryJill {

    private static Map<String, Blueprint> factories = new HashMap<>();

    public static <T> void factory(String alias, Class<T> clazz, Map<String, Object> attributes) {
        Blueprint blueprint = new Blueprint(clazz, attributes);
        factories.put(alias, blueprint);
    }

    public static <T> void factory(String alias, Class<T> clazz,
                                   Map<String, Object> attributes, Map<String, String> associations) {
        Map<String, Object> mutableAttributes = new HashMap<>(attributes);

        associations.forEach((String relationship, String factoryName) -> {
            try {
                mutableAttributes.put(relationship, build(factoryName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Blueprint blueprint = new Blueprint(clazz, mutableAttributes);
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

    private static <T> void setProperty(T newInstance, String property, Object potentialValue) throws InvocationTargetException, IllegalAccessException {
        Object value;

        if (potentialValue instanceof Function) {
            value = ((Function) potentialValue).apply(newInstance);
        } else {
            value = potentialValue;
        }

        BeanUtils.setProperty(newInstance, property, value);
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
