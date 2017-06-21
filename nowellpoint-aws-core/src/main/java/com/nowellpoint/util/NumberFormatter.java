package com.nowellpoint.util;

import java.text.DecimalFormat;

public class NumberFormatter {
	
	public static String formatFileSize(long size) {
	    String hrSize = null;

	    double b = size;
	    double k = size/1024.0;
	    double m = ((size/1024.0)/1024.0);
	    double g = (((size/1024.0)/1024.0)/1024.0);
	    double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

	    DecimalFormat format = new DecimalFormat("0.00");

	    if ( t > 1 ) {
	        hrSize = format.format(t).concat(" TB");
	    } else if ( g > 1 ) {
	        hrSize = format.format(g).concat(" GB");
	    } else if ( m > 1 ) {
	        hrSize = format.format(m).concat(" MB");
	    } else if ( k > 1 ) {
	        hrSize = format.format(k).concat(" KB");
	    } else {
	        hrSize = format.format(b).concat(" Bytes");
	    }

	    return hrSize;
	}
}