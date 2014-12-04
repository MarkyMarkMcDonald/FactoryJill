package FactoryJill;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
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

        blueprint.getAttributes().forEach((String property, Object value) -> setProperty(newInstance, property, value));

        overrides.forEach((String property, Object value) -> setProperty(newInstance, property, value));

        return newInstance;
    }

    private static <T> void setProperty(T newInstance, String property, Object potentialValue) {
        Object value;

        if (potentialValue instanceof Function) {
            value = ((Function) potentialValue).apply(newInstance);
        } else {
            value = potentialValue;
        }

        try {
            String setter = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
            Method matchingMethod = getMethodByName(newInstance, setter);

            matchingMethod.invoke(newInstance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> Method getMethodByName(T newInstance, String setter) {
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
            throw new NotImplementedException();
        }
    }
}
