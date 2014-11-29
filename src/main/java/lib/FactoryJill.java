package lib;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FactoryJill {

    private static Map<String, Blueprint> factories = new HashMap<>();

    public static <T> void factory(String alias, Class<T> clazz, Map<String, Object> attributes) {
        Blueprint blueprint = new Blueprint(clazz, attributes);
        factories.put(alias, blueprint);
    }

    public static Object build(String alias) throws Exception {
        return build(alias, new HashMap<>());
    }

    public static <T> T build(String factoryName, Map<String, Object> overrides) throws Exception {
        Blueprint<T> blueprint = factories.get(factoryName);
        Class<T> clazz = blueprint.getClazz();
        Constructor<?> constructor = clazz.getConstructor();
        T newInstance = (T) constructor.newInstance();

        blueprint.getAttributes().forEach((String property, Object value) -> overrideProperty(newInstance, property, value));

        overrides.forEach((String property, Object value) -> overrideProperty(newInstance, property, value));

        return newInstance;
    }

    private static <T> void overrideProperty(T newInstance, String property, Object value) {
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
