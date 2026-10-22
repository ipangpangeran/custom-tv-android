package com.gathin.tvhome;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Calculates prayer times for Bogor (and other Indonesian cities) completely offline
 * using standard astronomical algorithms according to Kementerian Agama Republik Indonesia (Kemenag RI) standards:
 * - Subuh: Sun altitude -20.0°
 * - Dhuhr: Solar noon + 2 min buffer
 * - Asr: Shadow ratio 1.0 (Shafi'i)
 * - Maghrib: Sun altitude -0.833° (sunset) + 2 min buffer
 * - Isha: Sun altitude -18.0°
 */
public final class PrayerTimes {

    public static final class Schedule {
        public final int subuhHour, subuhMin;
        public final int terbitHour, terbitMin;
        public final int dzuhurHour, dzuhurMin;
        public final int asharHour, asharMin;
        public final int maghribHour, maghribMin;
        public final int isyaHour, isyaMin;

        public Schedule(int subuhHour, int subuhMin, int terbitHour, int terbitMin,
                        int dzuhurHour, int dzuhurMin, int asharHour, int asharMin,
                        int maghribHour, int maghribMin, int isyaHour, int isyaMin) {
            this.subuhHour = subuhHour;
            this.subuhMin = subuhMin;
            this.terbitHour = terbitHour;
            this.terbitMin = terbitMin;
            this.dzuhurHour = dzuhurHour;
            this.dzuhurMin = dzuhurMin;
            this.asharHour = asharHour;
            this.asharMin = asharMin;
            this.maghribHour = maghribHour;
            this.maghribMin = maghribMin;
            this.isyaHour = isyaHour;
            this.isyaMin = isyaMin;
        }

        public String subuh() { return format(subuhHour, subuhMin); }
        public String terbit() { return format(terbitHour, terbitMin); }
        public String dzuhur() { return format(dzuhurHour, dzuhurMin); }
        public String ashar() { return format(asharHour, asharMin); }
        public String maghrib() { return format(maghribHour, maghribMin); }
        public String isya() { return format(isyaHour, isyaMin); }

        private static String format(int h, int m) {
            return String.format("%02d:%02d", h, m);
        }

        public static final class NextPrayer {
            public final String name;
            public final String time;
            public final int minutesLeft;
            public final int index; // 0=Subuh, 1=Dzuhur, 2=Ashar, 3=Maghrib, 4=Isya

            public NextPrayer(String name, String time, int minutesLeft, int index) {
                this.name = name;
                this.time = time;
                this.minutesLeft = minutesLeft;
                this.index = index;
            }
        }

        public NextPrayer getNextPrayer(int currentHour, int currentMin) {
            int currentMins = currentHour * 60 + currentMin;

            int sMins = subuhHour * 60 + subuhMin;
            int dMins = dzuhurHour * 60 + dzuhurMin;
            int aMins = asharHour * 60 + asharMin;
            int mMins = maghribHour * 60 + maghribMin;
            int iMins = isyaHour * 60 + isyaMin;

            if (currentMins < sMins) {
                return new NextPrayer("Subuh", subuh(), sMins - currentMins, 0);
            } else if (currentMins < dMins) {
                return new NextPrayer("Dzuhur", dzuhur(), dMins - currentMins, 1);
            } else if (currentMins < aMins) {
                return new NextPrayer("Ashar", ashar(), aMins - currentMins, 2);
            } else if (currentMins < mMins) {
                return new NextPrayer("Maghrib", maghrib(), mMins - currentMins, 3);
            } else if (currentMins < iMins) {
                return new NextPrayer("Isya", isya(), iMins - currentMins, 4);
            } else {
                // Next is tomorrow's Subuh
                int left = (24 * 60 - currentMins) + sMins;
                return new NextPrayer("Subuh", subuh(), left, 0);
            }
        }

        public String getPrayerNow(int currentHour, int currentMin) {
            if (currentHour == subuhHour && currentMin == subuhMin) return "Subuh";
            if (currentHour == dzuhurHour && currentMin == dzuhurMin) return "Dzuhur";
            if (currentHour == asharHour && currentMin == asharMin) return "Ashar";
            if (currentHour == maghribHour && currentMin == maghribMin) return "Maghrib";
            if (currentHour == isyaHour && currentMin == isyaMin) return "Isya";
            return null;
        }
    }

    public static Schedule getScheduleFor(Calendar cal, double lat, double lng, double tz) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);

        if (m <= 2) {
            y -= 1;
            m += 12;
        }
        double a = Math.floor(y / 100.0);
        double b = 2.0 - a + Math.floor(a / 4.0);
        double jd = Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + b - 1524.5;

        double dJulian = jd - 2451545.0;
        double g = fixAngle(357.529 + 0.98560028 * dJulian);
        double q = fixAngle(280.459 + 0.98564736 * dJulian);
        double L = fixAngle(q + 1.915 * Math.sin(Math.toRadians(g)) + 0.020 * Math.sin(Math.toRadians(2 * g)));
        double e = 23.439 - 0.00000036 * dJulian;
        double dec = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(e)) * Math.sin(Math.toRadians(L))));
        double ra = Math.toDegrees(Math.atan2(Math.cos(Math.toRadians(e)) * Math.sin(Math.toRadians(L)), Math.cos(Math.toRadians(L)))) / 15.0;
        ra = fixHour(ra);
        double eqt = q / 15.0 - ra;
        if (eqt > 12) eqt -= 24;
        if (eqt < -12) eqt += 24;

        double noon = 12.0 + tz - (lng / 15.0) - eqt;
        double bufferHours = 2.0 / 60.0; // 2 minutes Kemenag safety buffer

        double hSubuh = hourAngle(20.0, lat, dec);
        double hTerbit = hourAngle(0.833, lat, dec);

        // Ashar: shadow ratio 1
        double term = 1.0 + Math.tan(Math.toRadians(Math.abs(lat - dec)));
        double altAshar = Math.toDegrees(Math.atan(1.0 / term));
        double cosHAshar = (Math.sin(Math.toRadians(altAshar)) - Math.sin(Math.toRadians(lat)) * Math.sin(Math.toRadians(dec)))
                / (Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(dec)));
        double hAshar = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, cosHAshar))));

        double hIsya = hourAngle(18.0, lat, dec);

        double subuhTime = noon - (hSubuh / 15.0) + bufferHours;
        double terbitTime = noon - (hTerbit / 15.0) - bufferHours;
        double dzuhurTime = noon + bufferHours;
        double asharTime = noon + (hAshar / 15.0) + bufferHours;
        double maghribTime = noon + (hTerbit / 15.0) + bufferHours;
        double isyaTime = noon + (hIsya / 15.0) + bufferHours;

        int[] s = toHourMinute(subuhTime);
        int[] tb = toHourMinute(terbitTime);
        int[] dz = toHourMinute(dzuhurTime);
        int[] as = toHourMinute(asharTime);
        int[] mg = toHourMinute(maghribTime);
        int[] is = toHourMinute(isyaTime);

        return new Schedule(s[0], s[1], tb[0], tb[1], dz[0], dz[1], as[0], as[1], mg[0], mg[1], is[0], is[1]);
    }

    private static double hourAngle(double alpha, double lat, double dec) {
        double cosH = (-Math.sin(Math.toRadians(alpha)) - Math.sin(Math.toRadians(lat)) * Math.sin(Math.toRadians(dec)))
                / (Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(dec)));
        cosH = Math.max(-1.0, Math.min(1.0, cosH));
        return Math.toDegrees(Math.acos(cosH));
    }

    private static double fixAngle(double a) {
        a = a - 360.0 * Math.floor(a / 360.0);
        return a < 0 ? a + 360.0 : a;
    }

    private static double fixHour(double h) {
        h = h - 24.0 * Math.floor(h / 24.0);
        return h < 0 ? h + 24.0 : h;
    }

    private static int[] toHourMinute(double time) {
        int h = (int) Math.floor(time);
        int m = (int) Math.round((time - h) * 60.0);
        if (m == 60) {
            h += 1;
            m = 0;
        }
        h = (h % 24 + 24) % 24;
        return new int[]{h, m};
    }
}
