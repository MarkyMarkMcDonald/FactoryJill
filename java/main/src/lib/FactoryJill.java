package lib;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FactoryJill {

    public static <T> T build(Class<T> clazz, Map<String, Object>... overrides) throws Exception {
        Constructor<?> constructor = clazz.getConstructor();
        T newInstance = (T) constructor.newInstance();

        for (Map<String, Object> override : overrides) {
            override.forEach((String property, Object value) -> {
                overrideProperty(newInstance, property, value);
            });
        }

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

    public static <T> Map<String, T> override(String property, T value) {
        HashMap<String, T> propertyMap = new HashMap<>();
        propertyMap.put(property, value);
        return propertyMap;
    }
}
