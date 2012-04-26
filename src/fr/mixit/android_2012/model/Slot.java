package fr.mixit.android_2012.model;

import fr.mixit.android_2012.utils.DateUtils;

/**
 * A slot is a time period (start - end) when something can be planned.
 */
public enum Slot {
	
	HeightThirtyNine(DateUtils.parse(8, 30), DateUtils.parse(9, 0)),
	NineTen(DateUtils.parse(9, 0), DateUtils.parse(10, 0)),
	TenEleven(DateUtils.parse(10, 0), DateUtils.parse(11, 0)),
	ElevenNoon(DateUtils.parse(11, 0), DateUtils.parse(12, 0)),
	MidDayBreak(DateUtils.parse(12, 0), DateUtils.parse(14, 0)),
	TwoThree(DateUtils.parse(14, 0), DateUtils.parse(15, 0)),
	ThreeFour(DateUtils.parse(15, 0), DateUtils.parse(16, 0)),
	FourFive(DateUtils.parse(16, 0), DateUtils.parse(17, 0));

	public long start;
	public long end;

	Slot(long s, long e) {
		start = s;
		end = e;
	}
	
}
