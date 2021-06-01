package com.megumi.common;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;

public class JpaUtil {

    public static void copyNonNullProperties(Object src, Object tar) {
        BeanUtils.copyProperties(src, tar, getNullNames(src));
    }

    private static String[] getNullNames(Object source) {
        var obj = new BeanWrapperImpl(source);
        var pds = obj.getPropertyDescriptors();
        var nullVal = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            var val = obj.getPropertyValue(pd.getName());
            if (val == null) {
                nullVal.add(pd.getName());
            }
        }
        return nullVal.toArray(new String[0]);
    }
}
