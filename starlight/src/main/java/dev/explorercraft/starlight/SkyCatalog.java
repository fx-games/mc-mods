package dev.explorercraft.starlight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/// The stars and constellation figures from `assets/starlight/sky.txt`, which
/// `tools/make_sky.py` generates. Positions are equatorial: right ascension and declination
/// in degrees.
public final class SkyCatalog {
    public enum Kind { BACKGROUND, FIGURE, POLARIS }

    public record Star(double ra, double dec, float mag, Kind kind) {
        /// Unit vector in the equatorial frame: x towards RA 0h, y towards RA 6h, z to the
        /// celestial north pole.
        public float[] direction() {
            double a = Math.toRadians(ra), d = Math.toRadians(dec);
            return new float[]{(float) (Math.cos(d) * Math.cos(a)), (float) (Math.cos(d) * Math.sin(a)), (float) Math.sin(d)};
        }
    }

    /// `lines` holds star index pairs, one pair per line of the stick figure.
    public record Figure(String abbr, int rank, int[] lines) {
        public String translationKey() {
            return "constellation.starlight." + abbr;
        }
    }

    public static final SkyCatalog INSTANCE = load();

    public final List<Star> stars;
    public final List<Figure> figures;

    private SkyCatalog(List<Star> stars, List<Figure> figures) {
        this.stars = List.copyOf(stars);
        this.figures = List.copyOf(figures);
    }

    private static SkyCatalog load() {
        var stars = new ArrayList<Star>();
        var figures = new ArrayList<Figure>();
        var stream = SkyCatalog.class.getResourceAsStream("/assets/starlight/sky.txt");
        if (stream == null) throw new IllegalStateException("assets/starlight/sky.txt missing from the jar");

        try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] f = line.split(" ");
                switch (f[0]) {
                    case "S" -> stars.add(new Star(Double.parseDouble(f[1]), Double.parseDouble(f[2]),
                            Float.parseFloat(f[3]), Kind.values()[Integer.parseInt(f[4])]));
                    case "C" -> {
                        int[] lines = new int[f.length - 3];
                        for (int i = 0; i < lines.length; i++) lines[i] = Integer.parseInt(f[i + 3]);
                        figures.add(new Figure(f[1], Integer.parseInt(f[2]), lines));
                    }
                    default -> { } // comments
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new SkyCatalog(stars, figures);
    }
}
