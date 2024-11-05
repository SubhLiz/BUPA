package com.incture.bupa.utils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains utility functions to be used by Services
 * 
 * @version R1
 */

public class ServicesUtil {

	// private static final Logger logger =
	// LoggerFactory.getLogger(ServicesUtil.class);

	public static final String NOT_APPLICABLE = "N/A";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static boolean isEmpty(Object[] objs) {
		if (objs == null || objs.length == 0) {
			return true;
		}
		return false;
	}

	public static String NAME = "hello";

	public static String getName() {
		return "Hello";
	}

	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		} else if (o.toString().equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Collection<?> o) {
		if (o == null || o.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Integer str) {
		if (str == null || str == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(StringBuffer sb) {
		if (sb == null || sb.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(StringBuilder sb) {
		if (sb == null || sb.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Element nd) {
		if (nd == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(NamedNodeMap nd) {
		if (nd == null || nd.getLength() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Node nd) {
		if (nd == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(NodeList nd) {
		if (nd == null || nd.getLength() == 0) {
			return true;
		}
		return false;
	}

	public static String getStringFromList(List<String> stringList) {
		String returnString = "";
		for (String st : stringList) {
			returnString = returnString + "'" + st.trim() + "',";
		}
		return returnString.substring(0, returnString.length() - 1);
	}

	public static String getStringFromListForAls(List<String> stringList) {
		String returnString = "";
		if (!isEmpty(stringList)) {
			for (String st : stringList) {
				returnString = returnString + "" + st.trim() + ",";
			}
			returnString = returnString.substring(0, returnString.length() - 1);
		}
		return returnString;
	}

	public static String getStringFromList(String[] stringList) {
		String returnString = "";
		for (String st : stringList) {
			returnString = returnString + "'" + st.trim() + "',";
		}
		return returnString.substring(0, returnString.length() - 1);
	}

	public static String getStringForInQuery(String inputString) {
		String returnString = "";
		if (!ServicesUtil.isEmpty(inputString)) {
			if (inputString.contains(",")) {
				returnString = getStringFromList(inputString.split(","));
			} else {
				returnString = "'" + inputString.trim() + "'";
			}
		}
		return returnString;
	}

	public static String getCSV(Object... objs) {
		if (!isEmpty(objs)) {
			if (objs[0] instanceof Collection<?>) {
				return getCSVArr(((Collection<?>) objs[0]).toArray());
			} else {
				return getCSVArr(objs);
			}

		} else {
			return "";
		}
	}

	private static String getCSVArr(Object[] objs) {
		if (!isEmpty(objs)) {
			StringBuffer sb = new StringBuffer();
			for (Object obj : objs) {
				sb.append(',');
				if (obj instanceof Field) {
					sb.append(extractFieldName((Field) obj));
				} else {
					sb.append(extractStr(obj));
				}
			}
			sb.deleteCharAt(0);
			return sb.toString();
		} else {
			return "";
		}
	}

	public static String buildNoRecordMessage(String queryName, Object... parameters) {
		StringBuffer sb = new StringBuffer("No Record found for query: ");
		sb.append(queryName);
		if (!isEmpty(parameters)) {
			sb.append(" for params:");
			sb.append(getCSV(parameters));
		}
		return sb.toString();
	}

	public static String extractStr(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String extractFieldName(Field o) {
		return o == null ? "" : o.getName();
	}

	public static void setEmptyStringsDeep(Collection<?> object)
			throws IllegalArgumentException, IllegalAccessException {
		for (Object object2 : object) {
			setEmptyStrings(object2);
		}
	}

	public static void setEmptyStrings(Object object) throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (String.class.equals(field.getType())) {
				field.setAccessible(true);
				if (field.get(object) == null) {
					field.set(object, " ");
				}
			}
		}
	}

	public static Date stringToDate(String dateString) {
		Date date1 = null;
		if (ServicesUtil.isEmpty(dateString))
			return null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		return date1;
	}

	public static Date strToDate(String dateString) {
		Date date1 = null;
		if (ServicesUtil.isEmpty(dateString))
			return null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		return date1;
	}

	public static Date strToDateWithTime(String dateString) {
		Date date1 = null;
		if (ServicesUtil.isEmpty(dateString))
			return null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		return date1;
	}

	// public static void main(String... strings) throws ParseException {
	//// long dddd = 1576733837415L;
	// long dddd = 1576731866233L;
	// Date today = new Date(dddd);
	//
	// //displaying this date on IST timezone
	// DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:SS z");
	// df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
	// String IST = df.format(today);
	// System.out.println("Date in Indian Timezone (IST) : " + IST);
	// Date parse = df.parse(IST);
	// long time = parse.getTime();
	// System.out.println(time);
	// System.out.println(dddd-time);
	//
	// Date d = new Date(1576693800000L);
	//// long time = d.getTime();
	// System.out.println("date : " + d);
	//
	// }

	// public static void main(String... strings) {
	// Map<String, Object> map = new HashMap();
	// map.put("aa", 2);
	// System.out.println(map.get(2));
	// System.out.println(map.get(null));
	//
	// String date = "2017-12-09";
	//
	// Date strToDate = strToDate(date);
	// System.out.println("str to date : " + strToDate);
	// System.out.println(strToDate.toString());
	// Date gmtDate = getGmtDate(strToDate);
	// System.out.println("gmt :: " + gmtDate);
	//
	// // Date stringToDate = strToDate("2017-12-09");
	// // System.err.println("ServicesUtil.main():: " + stringToDate);
	// // TimeZone the_time_zone = TimeZone.getDefault();
	// // int rawOffset = the_time_zone.getRawOffset();
	// //
	// // Integer offset = ZonedDateTime.now().getOffset().getTotalSeconds();
	// // Integer min = offset / 60;
	// // System.err.println("ServicesUtil.main() time zone :: " +
	// // the_time_zone);
	// // System.err.println("ServicesUtil.main() time rawOffset :: " +
	// // rawOffset);
	// // System.err.println("ServicesUtil.main() time offset :: " + offset);
	// // System.err.println("ServicesUtil.main() time min :: " + min);
	// //
	// // String offsetId =
	// //
	// the_time_zone.toZoneId().getRules().getStandardOffset(Instant.now()).getId();
	// // System.err.println("offsetId :: " + offsetId);
	// }

	public static Date stringToDatewithSTZD(String dateString) {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(dateString);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		return date1;
	}

	public static String dateToString(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return sdf.format(date);
		}

		return "";
	}

	public static String dateToStringWithoutT(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String dateToStringWithoutTFormatddmmyyyy(Date date) {
		if (!isEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.format(date);
		}
		return "";
	}

	public static Date getGMTDate() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		Date dateGMT = cal.getTime();
		return dateGMT;
	}

	// method to check String is containing only alphabet or not.
	public static boolean isStringOnlyAlphabet(String str) {
		return ((str != null) && (!str.equals("")) && (str.matches("^[a-zA-Z]*$")));
	}

	public static void extractingWord(String input) {
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			System.err.print(matcher.group());
		}
	}

	public static String appendLeadingCharacters(char c, int len, String val) {
		if (!ServicesUtil.isEmpty(val)) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len - val.length(); i++) {
				sb.append(c);

			}
			sb.append(val);
			return sb.toString();
		}

		return null;
	}

	public static String generatePassayPassword() {

		// chose a Character random from this String
		String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String smallLetters = "abcdefghijklmnopqrstuvxyz";
		String digits = "0123456789";
		String specialChar = "!@#$%^&*()_+";
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 2; i++) {
			int index = (int) (capitalLetters.length() * Math.random());
			sb.append(capitalLetters.charAt(index));

			index = (int) (smallLetters.length() * Math.random());
			sb.append(smallLetters.charAt(index));

			index = (int) (digits.length() * Math.random());
			sb.append(digits.charAt(index));

			index = (int) (specialChar.length() * Math.random());
			sb.append(specialChar.charAt(index));

			index = (int) (alphaNumericString.length() * Math.random());
			sb.append(alphaNumericString.charAt(index));
		}

		System.err.println(sb.toString());

		return sb.toString();
	}

	public static Date getGmtDate(Date date) {
		if (isEmpty(date))
			return null;
		Date gmtDate = null;
		try {
			DateFormat gmtFormat = new SimpleDateFormat();
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);
			gmtDate = new SimpleDateFormat().parse(gmtFormat.format(date));
		} catch (ParseException e) {
			System.err.println("ServicesUtil.getGmtDate() : error message ::  " + e.getMessage());
		}
		return gmtDate;
	}

	// milliseconds to datetime
	public static Date millisecondsToDateTime(Long milliseconds) {
		if (ServicesUtil.isEmpty(milliseconds))
			return null;

		// long d = 1588098600000l;
		Date date = new Date(milliseconds);
		// System.out.println("SupplierInfoDto.main() date : " + date);
		// Date date = null;
		// logger.info("ServicesUtil.millisecondsToDateTime() : milliseconds ::
		// " + milliseconds);
		// Instant instant = Instant.ofEpochMilli(milliseconds);
		// LocalDateTime localDateTime =
		// instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		// logger.info("ServicesUtil.millisecondsToDateTime() : localDateTime ::
		// " + localDateTime);
		// date =
		// Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		System.err.println(
				"ServicesUtil.millisecondsToDateTime() : milliseconds : " + milliseconds + " = date :: " + date);
		return date;
	}

	// date to milliseconds
	public static Long dateTimeToMilliseconds(Date date) {
		if (ServicesUtil.isEmpty(date))
			return null;
		// logger.info("ServicesUtil.dateTimeToMilliseconds(): date :: " +
		// date);
		long timeInMillis = date.getTime();
		System.err.println(
				"ServicesUtil.dateTimeToMilliseconds(): date :: " + date + " = milliSeconds :: " + timeInMillis);
		return timeInMillis;
	}

}
