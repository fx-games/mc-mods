package dev.explorercraft.starlight;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import java.time.LocalDate;

/// The sky has to be the one over Kirchzarten: checked against what an almanac says.
public class StarlightGameTest {
    private static SkyCatalog.Star brightestIn(GameTestHelper helper, String abbr) {
        var sky = SkyCatalog.INSTANCE;
        var figure = sky.figures.stream().filter(f -> f.abbr().equals(abbr)).findFirst()
                .orElseThrow(() -> helper.assertionException(abbr + " missing from the catalogue"));
        SkyCatalog.Star best = null;
        for (int i : figure.lines()) {
            var star = sky.stars.get(i);
            if (best == null || star.mag() < best.mag()) best = star;
        }
        return best;
    }

    @GameTest
    public void catalogueIsConsistent(GameTestHelper helper) {
        var sky = SkyCatalog.INSTANCE;
        if (sky.stars.stream().filter(s -> s.kind() == SkyCatalog.Kind.POLARIS).count() != 1) {
            throw helper.assertionException("expected exactly one Polaris");
        }
        for (var figure : sky.figures) {
            for (int i : figure.lines()) {
                if (i < 0 || i >= sky.stars.size() || sky.stars.get(i).kind() == SkyCatalog.Kind.BACKGROUND) {
                    throw helper.assertionException(figure.abbr() + " points at star " + i + ", not a figure star");
                }
            }
        }
        helper.succeed();
    }

    @GameTest
    public void polarisStandsAtTheLatitude(GameTestHelper helper) {
        var polaris = SkyCatalog.INSTANCE.stars.stream().filter(s -> s.kind() == SkyCatalog.Kind.POLARIS).findFirst().orElseThrow();
        for (double hour = 0; hour < 24; hour += 3) {
            double alt = SkyClock.altitude(polaris, SkyClock.localSiderealDegrees(LocalDate.of(2026, 10, 16), hour));
            if (Math.abs(alt - SkyClock.LATITUDE) > 1.0) {
                throw helper.assertionException("Polaris at " + alt + "° at " + hour + "h, expected about " + SkyClock.LATITUDE);
            }
        }
        helper.succeed();
    }

    @GameTest
    public void orionIsAWinterConstellation(GameTestHelper helper) {
        var betelgeuse = brightestIn(helper, "Ori");
        // midnight in January: Rigel (dec -8, so at most 34 up here) high in the south-west; in July well down
        double january = SkyClock.altitude(betelgeuse, SkyClock.localSiderealDegrees(LocalDate.of(2027, 1, 15), 0));
        double july = SkyClock.altitude(betelgeuse, SkyClock.localSiderealDegrees(LocalDate.of(2026, 7, 15), 0));
        if (january < 20 || july > -10) {
            throw helper.assertionException("Orion's brightest star at " + january + "° in January, " + july + "° in July");
        }
        helper.succeed();
    }

    @GameTest
    public void starsRiseInTheEast(GameTestHelper helper) {
        // Sirius (RA 6h45m) culminates at local midnight early in January; six hours earlier it is rising
        var sirius = brightestIn(helper, "CMa");
        var matrix = SkyClock.equatorialToWorld(SkyClock.localSiderealDegrees(LocalDate.of(2027, 1, 1), 19));
        float[] d = sirius.direction();
        var world = matrix.transformDirection(new org.joml.Vector3f(d[0], d[1], d[2]));
        if (world.x <= 0) {
            throw helper.assertionException("Sirius in the evening at " + world + ", expected east (+x)");
        }
        helper.succeed();
    }
}
