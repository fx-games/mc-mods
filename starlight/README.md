# Starlight

The night sky over **Kirchzarten** in the Black Forest (47.97° N, 7.92° E), for Minecraft
**26.2** (Fabric).

Vanilla scatters 1500 random stars. Starlight puts real ones in their place: every constellation
that rises over Kirchzarten, drawn with the same square, blocky stars Minecraft always had. Stars
in a constellation figure are drawn bigger and brighter than the rest, so the Big Dipper,
Cassiopeia and Orion are the first things you see; the ~700 fainter background stars only fill
in.

- **The real sky, turning.** The sky follows today's date and the Minecraft time of day. Orion
  climbs out of the east on winter evenings, the Summer Triangle stands overhead in July, and the
  whole dome wheels around the pole through the night, as it does in Kirchzarten.
- **Polaris** sits still at 48° above the northern horizon, drawn as a small pixel sparkle so you
  can always find north.
- **Look through a spyglass** and the stick figures of the constellations fade in as faint blue
  lines between their stars, with the name of the constellation you are looking at under the
  crosshair (English or German, following the game language).
- **Adjustable spyglass zoom.** Scroll while looking through a spyglass to zoom between 2× (a
  whole constellation in view) and 50×; vanilla's fixed 10× is where it starts. The hotbar stays
  put while you scroll, the spyglass remembers the last zoom, and turning slows down as you zoom
  in so the view moves at the same pace on screen at every level.

The sun and the moon are left as vanilla draws them. The time of day is read as local solar time,
so the stars the sun sits among are the ones it really is in on that date.

Client side only: install it on the client, servers do not need it.

## Debug commands

| Command | What |
|---|---|
| `/starlight` or `/starlight status` | Date, local sidereal time, Polaris's altitude, star brightness, spyglass overlay strength and the constellation under the crosshair |
| `/starlight date 2026-12-24` | Shows the sky of another date; `/starlight date today` follows the real calendar again |

Client commands, so no permission level is needed. Combine with `/time set midnight` to look
at any night of the year.

## Tests

```bash
./gradlew runGametest
```

Headless. Checks the catalogue loads with exactly one Polaris and consistent figures, that
Polaris stands at Kirchzarten's latitude all night, that Orion is up on January midnights and
down on July ones, and that stars rise in the east.

## Regenerating the sky

```bash
python3 tools/make_sky.py   # stars, figures and constellation names
python3 tools/make_icon.py  # mod icon
```

## Credits

Star positions, magnitudes and constellation figures from
[d3-celestial](https://github.com/ofrohn/d3-celestial) by **Olaf Frohn** (BSD-3-Clause), built on
the Hipparcos catalogue.
