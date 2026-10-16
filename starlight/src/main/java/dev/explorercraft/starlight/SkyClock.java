package dev.explorercraft.starlight;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.time.LocalDate;
import java.time.ZoneId;

/// Where the stars stand over Kirchzarten for a calendar date and a Minecraft time of day.
///
/// The date is the real one (Europe/Berlin) unless `/starlight date` pinned another. The hour
/// comes from the game's own star angle, read as local mean solar time: the Minecraft sun is
/// overhead at noon, and so is the mean sun in the sky this builds, which keeps the zodiac
/// where the sun actually is.
public final class SkyClock {
    public static final double LATITUDE = 47.97;
    public static final double LONGITUDE = 7.92;
    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    private static LocalDate pinnedDate;

    private SkyClock() {
    }

    public static LocalDate date() {
        return pinnedDate != null ? pinnedDate : LocalDate.now(ZONE);
    }

    /// null follows the real calendar again
    public static void pinDate(LocalDate date) {
        pinnedDate = date;
    }

    public static boolean isPinned() {
        return pinnedDate != null;
    }

    /// Minecraft's star angle is 0 at noon and turns a full circle a day.
    public static double solarHours(float starAngleRadians) {
        return Math.floorMod((long) Math.floor((12 + Math.toDegrees(starAngleRadians) / 15) * 3600), 86400) / 3600.0;
    }

    /// Local sidereal time in degrees: the right ascension standing on the meridian.
    public static double localSiderealDegrees(LocalDate date, double localSolarHours) {
        double universalHours = localSolarHours - LONGITUDE / 15;
        // days since J2000.0 (2000-01-01 12:00 UT)
        double days = date.toEpochDay() - 10957.5 + universalHours / 24;
        double gmst = 280.46061837 + 360.98564736629 * days;
        return ((gmst + LONGITUDE) % 360 + 360) % 360;
    }

    /// Turns equatorial unit vectors into Minecraft's world axes (+x east, +y up, +z south).
    public static Matrix4f equatorialToWorld(double siderealDegrees) {
        float sin = (float) Math.sin(Math.toRadians(LATITUDE)), cos = (float) Math.cos(Math.toRadians(LATITUDE));
        // columns: the equator on the meridian, the equator 6h east of it, and the north pole
        return new Matrix4f(
                0, cos, sin, 0,
                1, 0, 0, 0,
                0, sin, -cos, 0,
                0, 0, 0, 1)
                .rotateZ((float) -Math.toRadians(siderealDegrees));
    }

    /// Altitude above the horizon in degrees.
    public static double altitude(SkyCatalog.Star star, double siderealDegrees) {
        float[] d = star.direction();
        return Math.toDegrees(Math.asin(equatorialToWorld(siderealDegrees).transformDirection(new Vector3f(d[0], d[1], d[2])).y));
    }
}
