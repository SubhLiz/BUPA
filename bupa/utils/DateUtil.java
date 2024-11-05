package com.incture.bupa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	

//	static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String dateToString(Date input) {
		return formatter.format(input);
		}

		public static Date stringToDate(String input) {
		try {
		return formatter.parse(input);
		} catch (ParseException e) {
		e.printStackTrace();
		return null;
		}
		}
}
