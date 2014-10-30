package com.elvishjerricco.cctransport.peripheral.converter;

import java.util.HashMap;

public class ConversionFactory {
    public static interface Converter<T> {
        public Object convert(T a);
    }

    public static <T> Object convert(T o) {
        if (o == null) {
            return null;
        }
        Converter<T> c = (Converter<T>)converters.get(o.getClass());
        if (c == null) {
            throw new IllegalArgumentException("Un-convertible object: " + o.getClass().getName());
        }

        return c.convert(o);
    }

    private static final HashMap<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();
    public static <T> void registerConverter(Converter<T> c, Class<T> cls) {
        if (converters.containsKey(c)) {
            throw new IllegalArgumentException("Class already registered for conversion");
        }

        converters.put(cls, c);
    }
}
