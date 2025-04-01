package org.optaplanner.examples.common.util;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * very general purpose and common utility for all the classes
 */
public class Util {
    private static StandardToStringStyle style = new StandardToStringStyle();

    public static StandardToStringStyle getStyle(){
        style.setFieldSeparator(", ");
        style.setUseClassName(false);
        style.setUseIdentityHashCode(false);
        return style;
    }

    public static String getToString(Object object){
//        return ReflectionToStringBuilder.toString(object, ToStringStyle.MULTI_LINE_STYLE);
        return new ReflectionToStringBuilder(object, Util.getStyle()).toString();
    }
}
