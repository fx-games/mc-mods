#!/usr/bin/env python3
"""Draw the climbing claws item icon and the Modrinth project icon.

    python3 tools/make_textures.py

The item is two 16x16 layers. `climbing_claws` is the bare metal, which the item model tints with
`minecraft:dyed_color` so one texture covers all six materials — it has to stay near-white, because
a tint multiplies and cannot brighten. `climbing_claws_grip` carries everything that must NOT take
the tint: the dark outline around the metal, and the leather hand loop. Keeping the outline out of
the tinted layer is what makes the dark materials readable — multiplied by copper or obsidian, a
grey outline turns into the same muddy colour as the metal it is supposed to separate.

The claws carry no outline on purpose: at three pixels apart, an outline on both sides of each
claw closes the gaps between them and the whole row turns to mush.
"""
import pathlib

from PIL import Image

OUT = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/climbingclaws"

OUTLINE = (58, 58, 58)

METAL_PALETTE = {
    ".": None,
    "n": None,                  # outline lives in the untinted layer
    "M": (255, 255, 255),       # lit face
    "m": (200, 200, 200),       # shaded face
}

OUTLINE_PALETTE = {
    ".": None,
    "n": OUTLINE,
    "M": None,
    "m": None,
}

CLAWS = [
    "................",
    "................",
    "................",
    "................",
    "................",
    ".nnnnnnnnnnnnnn.",
    ".nMMMMMMMMMMMMn.",
    ".nmmmmmmmmmmmmn.",
    ".nnMmnnMmnnMmnn.",
    "...Mm..Mm..Mm...",
    "..Mm..Mm..Mm....",
    "..Mm..Mm..Mm....",
    "..M...M...M.....",
    "..m...m...m.....",
    "................",
    "................",
]

GRIP_PALETTE = {
    ".": None,
    "o": (46, 32, 20),          # outline
    "l": (134, 94, 54),         # leather
}

GRIP = [
    "................",
    ".....oooooo.....",
    "....ollllllo....",
    "...olloooollo...",
    "..ollo....ollo..",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
]

# The project icon shows iron claws — the same tint the iron recipe stamps on the item.
IRON_TINT = 0xD8D8D8


def draw(art, palette, tint=None):
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(art):
        for x, char in enumerate(row):
            colour = palette[char]
            if colour:
                if tint:
                    colour = tuple(c * t // 255 for c, t in zip(colour, tint))
                image.putpixel((x, y), colour + (255,))
    return image


def claws(art, palette):
    """Flipped so the claws point up in the inventory, matching how they sit in the hand."""
    return draw(art, palette).transpose(Image.FLIP_TOP_BOTTOM)


def tint_image(image, tint):
    out = image.copy()
    pixels = out.load()
    for y in range(16):
        for x in range(16):
            r, g, b, a = pixels[x, y]
            if a:
                pixels[x, y] = (r * tint[0] // 255, g * tint[1] // 255, b * tint[2] // 255, a)
    return out


def main():
    items = OUT / "textures/item"
    metal = claws(CLAWS, METAL_PALETTE)

    untinted = claws(CLAWS, OUTLINE_PALETTE)
    untinted.alpha_composite(draw(GRIP, GRIP_PALETTE).transpose(Image.FLIP_TOP_BOTTOM))

    metal.save(items / "climbing_claws.png")
    untinted.save(items / "climbing_claws_grip.png")

    tint = tuple((IRON_TINT >> shift) & 0xFF for shift in (16, 8, 0))
    icon = tint_image(metal, tint)
    icon.alpha_composite(untinted)
    icon.resize((128, 128), Image.NEAREST).save(OUT / "icon.png")


if __name__ == "__main__":
    main()
