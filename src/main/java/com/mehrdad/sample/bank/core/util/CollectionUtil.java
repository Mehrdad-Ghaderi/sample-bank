package com.mehrdad.sample.bank.core.util;

import java.util.Collection;

/**
 * @author Ali Asghar Momeni Vesalian (momeni.vesalian@gmail.com)
 */
public final class CollectionUtil {

    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || collection.isEmpty();
    }

}
