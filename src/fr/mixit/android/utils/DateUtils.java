package fr.mixit.android.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

	public static long parse(int hour, int minute) {
		Calendar cal = GregorianCalendar.getInstance(Locale.FRANCE);
		cal.set(2012, 04, 26, hour, minute);
		return cal.getTimeInMillis();
	}

}
