#!/usr/bin/env python3
"""Draw the mod icon: the Big Dipper pointing at Polaris above Black Forest spruces.

    python3 tools/make_icon.py

Drawn on a 32x32 pixel grid, the way Minecraft textures are, and scaled up without smoothing.
"""
import pathlib
import struct
import zlib

GRID = 32
SCALE = 8
OUT = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/starlight/icon.png"

SKY_TOP = (12, 14, 38)
SKY_BOTTOM = (34, 44, 92)
HILL_FAR = (18, 30, 34)
HILL_NEAR = (10, 18, 20)
STAR = (255, 255, 240)
STAR_DIM = (170, 180, 210)
POLARIS = (255, 246, 196)
LINE = (88, 120, 190)

# Big Dipper, handle to bowl; the last two are the pointers whose line leads to Polaris.
DIPPER = [(3, 21), (6, 19), (9, 18), (12, 19), (13, 22), (18, 22), (18, 18)]
FAINT = [(26, 3), (4, 5), (15, 2), (29, 13), (9, 10), (23, 11), (2, 13), (27, 21), (11, 4)]
POLE = (20, 7)


def line(px, a, b, color):
    (x0, y0), (x1, y1) = a, b
    steps = max(abs(x1 - x0), abs(y1 - y0))
    for i in range(1, steps):
        x = round(x0 + (x1 - x0) * i / steps)
        y = round(y0 + (y1 - y0) * i / steps)
        px[y][x] = color


def main():
    px = [[None] * GRID for _ in range(GRID)]
    # banded gradient, four rows per band, like a texture would have it
    for y in range(GRID):
        t = (y // 4) / (GRID // 4 - 1)
        row = tuple(round(a + (b - a) * t) for a, b in zip(SKY_TOP, SKY_BOTTOM))
        for x in range(GRID):
            px[y][x] = row

    for a, b in zip(DIPPER, DIPPER[1:]):
        line(px, a, b, LINE)
    line(px, DIPPER[3], DIPPER[-1], LINE)  # close the bowl
    for x, y in FAINT:
        px[y][x] = STAR_DIM
    for x, y in DIPPER:
        px[y][x] = STAR

    x, y = POLE
    for dx, dy in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
        px[y + dy][x + dx] = POLARIS
    for dx, dy in ((2, 0), (-2, 0), (0, 2), (0, -2)):
        px[y + dy][x + dx] = STAR_DIM

    # rolling far hills, then a near ridge of spruces
    for x in range(GRID):
        far = 26 - (2 if 6 <= x <= 15 else 0) - (1 if 18 <= x <= 24 else 0)
        for y in range(far, GRID):
            px[y][x] = HILL_FAR
        for y in range(29, GRID):
            px[y][x] = HILL_NEAR
    for tx, height in ((3, 6), (8, 4), (12, 7), (20, 5), (25, 8), (29, 5)):
        for level in range(height):
            half = (height - level) // 2
            for dx in range(-half, half + 1):
                if 0 <= tx + dx < GRID:
                    px[28 - level + 1][tx + dx] = HILL_NEAR

    size = GRID * SCALE
    raw = b"".join(
        b"\x00" + b"".join(struct.pack("4B", *px[y // SCALE][x // SCALE], 255) for x in range(size))
        for y in range(size)
    )

    def chunk(tag, data):
        body = tag + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body))

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", size, size, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw, 9))
    png += chunk(b"IEND", b"")
    OUT.write_bytes(png)
    print(OUT)


if __name__ == "__main__":
    main()
