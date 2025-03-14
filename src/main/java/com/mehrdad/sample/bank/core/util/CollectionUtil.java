package com.mehrdad.sample.bank.core.util;

import java.util.Collection;

/**
 * Created by Mehrdad Ghaderi
 */
public final class CollectionUtil {

    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || collection.isEmpty();
    }

}
