package io.anuke.gwtref.client;

import com.google.gwt.core.client.GWT;

public class ReflectionCache{
    private static IReflectionCache instance = GWT.create(IReflectionCache.class);

    public static Type forName(String name) throws ClassNotFoundException{
        Type type = instance.forName(convert(name));
        if(type == null){
            throw new RuntimeException("Couldn't find Type for class '" + name + "'");
        }
        return type;
    }

    public static Type getType(Class clazz){
        if(clazz == null) return null;
        Type type = instance.forName(convert(clazz.getName()));
        if(type == null){
            throw new RuntimeException("Couldn't find Type for class '" + clazz.getName() + "'");
        }
        return type;
    }

    private static String convert(String className){
        if(className.startsWith("[")){
            int dimensions = 0;
            char c = className.charAt(0);
            String suffix = "";
            while(c == '['){
                dimensions++;
                suffix += "[]";
                c = className.charAt(dimensions);
            }
            char t = className.charAt(dimensions);
            switch(t){
                case 'Z':
                    return "boolean" + suffix;
                case 'B':
                    return "byte" + suffix;
                case 'C':
                    return "char" + suffix;
                case 'L':
                    return className.substring(dimensions + 1, className.length() - 1).replace('$', '.') + suffix;
                case 'D':
                    return "double" + suffix;
                case 'F':
                    return "float" + suffix;
                case 'I':
                    return "int" + suffix;
                case 'J':
                    return "long" + suffix;
                case 'S':
                    return "short" + suffix;
                default:
                    throw new IllegalArgumentException("Couldn't transform '" + className + "' to qualified source name");
            }
        }else{
            return className.replace('$', '.');
        }
    }

    public static Object newArray(Class componentType, int size){
        return instance.newArray(getType(componentType), size);
    }

    public static Object getFieldValue(Field field, Object obj) throws IllegalAccessException{
        return instance.get(field, obj);
    }

    public static void setFieldValue(Field field, Object obj, Object value) throws IllegalAccessException{
        instance.set(field, obj, value);
    }

    public static Object invoke(Method method, Object obj, Object[] params){
        return instance.invoke(method, obj, params);
    }

    public static int getArrayLength(Type type, Object obj){
        return instance.getArrayLength(type, obj);
    }

    public static Object getArrayElement(Type type, Object obj, int i){
        return instance.getArrayElement(type, obj, i);
    }

    public static void setArrayElement(Type type, Object obj, int i, Object value){
        instance.setArrayElement(type, obj, i, value);
    }
}
