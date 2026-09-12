#!/usr/bin/env bash
# Re-imports Camerapture into vendor/camerapture, the same way the current copy was made.
# Run from the photo-safari directory: ./vendor/import_camerapture.sh [commit-ish]
#
# What it does beyond copying: drops the loaders and optional integrations this mod does not
# ship (NeoForge, Cloth Config, Jade, First Person Model, Mod Menu), takes out the Tapestry
# multi-loader indirection, and splits the Fabric sources across main/client, which this
# project keeps apart and upstream does not.
set -euo pipefail

UPSTREAM=https://github.com/chrrs/camerapture.git
REF=${1:-08eae6e}
DEST=$(cd "$(dirname "$0")" && pwd)/camerapture
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

echo "==> cloning $UPSTREAM @ $REF"
git clone --quiet --branch 26.2 "$UPSTREAM" "$WORK/src"
git -C "$WORK/src" checkout --quiet "$REF"

echo "==> copying common + fabric into $DEST"
rm -rf "$DEST/src"
mkdir -p "$DEST/src/main/java" "$DEST/src/main/resources" "$DEST/src/client/java" "$DEST/src/client/resources"
cp -R "$WORK/src/common/src/main/java/me" "$DEST/src/main/java/"
cp -R "$WORK/src/common/src/main/resources/." "$DEST/src/main/resources/"
cp -R "$WORK/src/common/src/client/java/me" "$DEST/src/client/java/"
cp -R "$WORK/src/common/src/client/resources/." "$DEST/src/client/resources/"
cp -R "$WORK/src/fabric/src/main/java/me/chrr/camerapture/fabric" "$DEST/src/main/java/me/chrr/camerapture/"
cp "$WORK/src/fabric/src/main/resources/camerapture-fabric.mixins.json" "$DEST/src/client/resources/"
cp "$WORK/src/LICENSE" "$DEST/LICENSE"

echo "==> dropping optional integrations we do not ship"
rm -f "$DEST/src/main/java/me/chrr/camerapture/compat/JadePlugin.java" \
      "$DEST/src/main/java/me/chrr/camerapture/fabric/compat/ModMenuCompat.java" \
      "$DEST/src/client/java/me/chrr/camerapture/compat/ClothConfigScreenFactory.java" \
      "$DEST/src/client/java/me/chrr/camerapture/compat/FirstPersonModelCompat.java"
rmdir "$DEST/src/main/java/me/chrr/camerapture/compat" \
      "$DEST/src/main/java/me/chrr/camerapture/fabric/compat" \
      "$DEST/src/client/java/me/chrr/camerapture/compat" 2>/dev/null || true

echo "==> moving the client-only Fabric sources into the client source set"
mkdir -p "$DEST/src/client/java/me/chrr/camerapture/fabric"
mv "$DEST/src/main/java/me/chrr/camerapture/fabric/mixin" "$DEST/src/client/java/me/chrr/camerapture/fabric/"
mv "$DEST/src/main/java/me/chrr/camerapture/fabric/CameraptureClientFabric.java" \
   "$DEST/src/client/java/me/chrr/camerapture/fabric/"

echo "==> unhooking Tapestry"
python3 - "$DEST" <<'PY'
import pathlib, re, sys

root = pathlib.Path(sys.argv[1])
for path in root.rglob('*.java'):
    text = path.read_text()
    patched = re.sub(r'^import me\.chrr\.tapestry\.[^\n]*\n', '', text, flags=re.M)
    patched = re.sub(r'^@(FabricEntrypoint|Implementation)\([^)]*\)\n', '', patched, flags=re.M)
    if patched != text:
        path.write_text(patched)

# The two service lookups are the only real use: name the Fabric implementations outright.
path = root / 'src/main/java/me/chrr/camerapture/Camerapture.java'
text = path.read_text()
text = text.replace(
    '    public static final PlatformAdapter PLATFORM = Tapestry.implementation(id("platform_adapter"));\n'
    '    public static final NetworkAdapter NETWORK = Tapestry.implementation(id("network_adapter"));',
    '    // Vendored: upstream looks these two up through its Tapestry multi-loader service layer.\n'
    '    // This copy is Fabric-only, so the Fabric implementations are simply named outright.\n'
    '    public static final PlatformAdapter PLATFORM = new FabricPlatformAdapter();\n'
    '    public static final NetworkAdapter NETWORK = new FabricNetworkAdapter();')
text = text.replace(
    'import me.chrr.camerapture.config.ConfigManager;',
    'import me.chrr.camerapture.config.ConfigManager;\n'
    'import me.chrr.camerapture.fabric.FabricNetworkAdapter;\n'
    'import me.chrr.camerapture.fabric.FabricPlatformAdapter;')
path.write_text(text)

# The only caller of the dropped First Person Model compat.
path = root / 'src/client/java/me/chrr/camerapture/CameraptureClient.java'
text = path.read_text()
text = text.replace('import me.chrr.camerapture.compat.FirstPersonModelCompat;\n', '')
text = text.replace(
    '        if (Camerapture.PLATFORM.isModLoaded("firstperson")) {\n'
    '            FirstPersonModelCompat.register();\n'
    '        }\n\n', '')
path.write_text(text)
PY

echo "==> done. Build with ./gradlew build, and run ./gradlew runGametest."
