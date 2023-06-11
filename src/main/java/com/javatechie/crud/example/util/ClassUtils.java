package com.javatechie.crud.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ClassUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);


    public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> rawCollection) {

        List<T> result = new ArrayList<>(rawCollection.size());
        for (Object o : rawCollection) {
            try {
                result.add(clazz.cast(o));
            } catch (ClassCastException e) {
                LOGGER.error("Unable to cast the object with type: {}", clazz);
                throw new ClassCastException(String.format("Invalid data found unable to cast to type: %s", clazz));
            }
        }
        return result;
    }

}
