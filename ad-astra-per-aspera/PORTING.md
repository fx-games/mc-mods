# Porting notes: Ad Astra 1.21.1 (multiloader) -> Ad Astra Per Aspera, Fabric 26.2

Target: Minecraft 26.2 (unobfuscated, official Mojang names), Fabric Loader 0.19.3, Fabric API 0.161.0+26.2,
Java 25, Loom 1.18-SNAPSHOT, ResourcefulLib 5.0.4, ResourcefulConfig 5.0.1, Team Reborn Energy 5.0.0.

## Reference sources (read-only, grep these instead of guessing APIs)
- Minecraft 26.2 sources: `C:/Project/ref/mc/net/minecraft/...`
- Fabric API 26.2 sources: `C:/Project/ref/fapi/net/fabricmc/fabric/...`
- ResourcefulLib / ResourcefulConfig 26.2 sources: `C:/Project/ref/rl/com/teamresourceful/...`
- Original 1.21.1 project (incl. NeoForge code, useful to see intent): `C:/Project/Ad-Astra/src_tmp/`

Always confirm a class/method exists in the reference sources before using it. Many things were renamed in 26.x.

## Build
- `./gradlew compileJava --console=plain > build.log 2>&1` (Gradle runs on JDK 25 via `org.gradle.java.home`).
- `-Xmaxerrs 5000`; filter the log to your own files: `grep -A3 'adastra\\client\\' build.log`.
- javac reports errors in phases: fixing symbol errors can reveal new type errors. Iterate until your files are clean.

## Decisions already made
- Fabric only. NeoForge code dropped. REI/JEI compat dropped (not ported yet).
- Common Storage Lib replaced by `earth.terrarium.adastra.common.storage` (same class names: ValueStorage,
  SimpleValueStorage, CommonStorage, SimpleFluidStorage, FluidResource, ResourceStack, FluidAmounts, FluidApi,
  EnergyApi, ItemContext/ModifyOnlyContext/IsolatedSlotContext, TransferUtil, UpdateManager, DataManager,
  SizedFluidIngredient). It bridges to Fabric Transfer API + Team Reborn Energy. Fix call sites to match it;
  only change the storage package itself if truly needed (and say so in your report).
  `ItemContext` is now an abstract class: use `ctx.stack()` (not `getResource()` / `mainSlot().toItemStack()`).
- Mod id stays `ad_astra`; display name is "Ad Astra Per Aspera".
- `@Expect/@Actual` platform shims were merged into `PlatformUtils` / `ClientPlatformUtils` (Fabric impl only).
- Already globally applied: `ResourceLocation` -> `Identifier` (`net.minecraft.resources.Identifier`),
  `level.isClientSide` -> `level.isClientSide()`.

## Known 26.x API changes (add to this list as you discover more)
- GUI: `GuiGraphics` -> `GuiGraphicsExtractor`; `Screen.render` -> `extractRenderState`, `renderBg` ->
  `extractBackground`/`extractContents`, `drawString` -> `text(...)`; pose is `Matrix3x2fStack`.
- `InteractionResultHolder` removed -> `InteractionResult` (e.g. `InteractionResult.SUCCESS`, `.CONSUME`, `.PASS`).
- `ArmorItem`/`ArmorMaterial` reworked (`net.minecraft.world.item.equipment`); armor is a normal `Item` with
  `Item.Properties#humanoidArmor(...)` / equippable component.
- `FastColor` -> `ARGB`.
- Recipes: `RecipeInput`, `RecipeManager` only on the server (`ServerLevel#recipeAccess()`), new `RecipeSerializer`
  shape; ResourcefulLib `common.recipe` package is gone.
- Item models are data driven (`assets/<ns>/items/*.json`), `BakedModel` is gone.
- `Item.Properties`/`Block.Properties` need `.setId(ResourceKey)` (see `ModItems.props`/`ModBlocks.props`); BlockItems
  also `.useBlockDescriptionPrefix()`. `EntityType.Builder.build(ResourceKey)`; `new BlockEntityType<>(f, Set.of(..))`.
- `ResourceKey.location()` -> `identifier()`; `registryOrThrow/getHolderOrThrow` -> `lookupOrThrow/getOrThrow`;
  `net.minecraft.Util` -> `net.minecraft.util.Util`.
- `RecipeSerializer` is a record `(MapCodec, StreamCodec)`; `Recipe` needs `assemble/showNotification/group/
  placementInfo/recipeBookCategory` (defaults in `recipes/base/CodecRecipe`). Client recipe sync: Fabric
  `RecipeSynchronization.synchronizeRecipeSerializer` + `level.recipeAccess().getSynchronizedRecipes()`
  (`ModRecipeTypes.getAll`). `CachedCheck.getRecipeFor(input, ServerLevel)`. `BlastingRecipe`: `input()`,
  `assemble(SingleRecipeInput)`, `cookingTime()`. `Ingredient.of(TagKey)` gone; `Ingredient.items()` is a stream.
- Block entities: `loadAdditional(ValueInput)`/`saveAdditional(ValueOutput)` (`getIntOr`, `read/store(name, codec)`,
  `childrenList`); `ContainerHelper` has no provider arg. Drops on removal: `BlockEntity#preRemoveSideEffects`;
  `Block#onRemove` -> `affectNeighborsAfterRemoval(state, ServerLevel, pos, moved)`.
- Block: `updateShape(state, LevelReader, ScheduledTickAccess, pos, dir, neighborPos, neighborState, RandomSource)`;
  `neighborChanged(.., @Nullable Orientation, ..)`; `wasExploded(ServerLevel, ..)`;
  `getAnalogOutputSignal(state, level, pos, Direction)`; `entityInside(.., InsideBlockEffectApplier, boolean)`;
  `Block#appendHoverText` removed (use `TooltipBlock` + `ModBlockItem`). `EnumProperty` is final -> `EnumProperty.create`;
  `DirectionProperty` -> `EnumProperty<Direction>`. `LeavesBlock` abstract -> `TintedParticleLeavesBlock`.
- Item: `appendHoverText(stack, ctx, TooltipDisplay, Consumer<Component>, flag)`; `inventoryTick(stack, ServerLevel,
  Entity, @Nullable EquipmentSlot)` (server only); `UseAnim` -> `ItemUseAnimation`; `ItemInteractionResult` ->
  `InteractionResult` (`TRY_WITH_EMPTY_HAND`). `ClickType` -> `ContainerInput`; `Slot.getNoItemIcon` returns `Identifier`.
- `displayClientMessage(x, true)` -> `sendOverlayMessage`; `level.random` -> `getRandom()`; `player.server` ->
  `level().getServer()`; `Level.getDayTime` -> `getOverworldClockTime`; `GameProfile` is a record (`id()/name()`).
- `PaintingVariant` in `world.entity.decoration.painting` (title/author `Optional<Component>`); `STRUCTURE_PROCESSOR`
  registry holds `MapCodec<? extends StructureProcessor>`. CompoundTag getters return `Optional`.
- ResourcefulLib: `FluidProperties.builder()` lost still/flowing/overlay/tint (now `ClientFluidProperties.builder()`);
  ResourcefulConfig `EntryType` removed.
- GUI details: `graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, w, h, texW, texH)`,
  `blitSprite(RenderPipelines.GUI_TEXTURED, id|TextureAtlasSprite, x, y, w, h[, argbTint])` (tint replaces
  `RenderSystem.setShaderColor`); text colors MUST include alpha (`0xFF......`) or nothing is drawn;
  `renderOutline` -> `outline`, `drawCenteredString` -> `centeredText`; `enableScissor(x0, y0, x1, y1)` (corners,
  not size); tooltips via `graphics.setTooltipForNextFrame(...)` / `setComponentTooltipForNextFrame(font, lines, x, y)`;
  cursors via `graphics.requestCursor(CursorTypes.POINTING_HAND)` (RL CursorScreen/CursorWidget are gone).
  Custom geometry: implement `GuiElementRenderState` and add it with `graphics.guiRenderState.addGuiElement(...)`
  (example: `client/utils/GuiLinesRenderState`). `AbstractContainerScreen.imageWidth/Height` are final: pass them to
  the 5-arg super constructor. `renderBg` -> `extractBackground(graphics, mouseX, mouseY, a)` (call super),
  `renderLabels` -> `extractLabels`, `hasClickedOutside` lost its button param.
- Input: `mouseClicked(MouseButtonEvent, boolean doubleClick)`, `mouseReleased(MouseButtonEvent)`,
  `mouseDragged(MouseButtonEvent, dx, dy)`, `onClick(MouseButtonEvent, boolean)`, `isValidClickButton(MouseButtonInfo)`,
  `AbstractButton.onPress(InputWithModifiers)`. `Screen.hasShiftDown()` -> `Minecraft.getInstance().hasShiftDown()`
  or `input.hasShiftDown()`.
- `Minecraft.setScreen(s)` / `Minecraft.screen` -> `Minecraft.getInstance().gui.setScreen(s)` / `gui.screen()`.
  `Minecraft.tell` -> `execute`. `net.minecraft.Util` -> `net.minecraft.util.Util`.
- `ResourceKey.location()` -> `identifier()`. `Level.getDayTime()` -> `getDefaultClockTime()` (or
  `getOverworldClockTime()`). `SoundInstance.getLocation()` -> `getIdentifier()`. `BlockPos.getCenter()` ->
  `Vec3.atCenterOf(pos)`. `Inventory.getArmor(i)` -> `player.getItemBySlot(EquipmentSlot.X)`.
- ResourcefulConfig 5: `@ConfigEntry` has no `type` attribute (drop `type = EntryType.X`).
- ResourcefulLib 5 client: `SelectionList`/`ListEntry`, `CursorScreen`, `RenderUtils.createScissorBox`,
  `ScreenUtils.setTooltip` are gone (use vanilla `ObjectSelectionList`, scissor, tooltip APIs above).
- Entities: save via `ValueInput`/`ValueOutput` (`getIntOr("k", 0)` etc.); `hurt` -> `hurtServer(ServerLevel, ..)`;
  `interact(Player, Hand, Vec3)`; `isControlledByLocalInstance` -> `isLocalInstanceAuthoritative`; lerp via
  `InterpolationHandler`/`getInterpolation()`; `teleport(TeleportTransition)` (was DimensionTransition); `moveTo` -> `snapTo`;
  `entity.is(TagKey)`; `causeFallDamage(double, float, DamageSource)`; `canBeCollidedWith(Entity)`; `level().random` -> `getRandom()`.
- `MobSpawnType` -> `EntitySpawnReason`, `EntityType.create(level, reason)`; `convertTo(type, ConversionParams.single(..), cb)`;
  `customServerAiStep(ServerLevel)`, `doHurtTarget(ServerLevel, Entity)`; `MobEffects.CONFUSION` -> `NAUSEA`.
- Entity packages moved: `vehicle.boat.AbstractBoat`, `vehicle.minecart`, `projectile.arrow`, `projectile.throwableitemprojectile`,
  `animal.golem`, `monster.zombie`, `npc.villager`, `npc.wanderingtrader`.
- Villager trades are data driven: `VillagerTrades.ItemListing`/`addOffersFromItemListings` gone; `updateTrades(ServerLevel)`;
  `VillagerData` record with `profession()` Holder. Wandering trader timers live in `WanderingTraderData` (SavedData).
- `GameRules` -> `world.level.gamerules.GameRules`, `level.getGameRules().get(GameRules.MOB_GRIEFING)`.
- `CustomSpawner.tick(ServerLevel, boolean)` returns void. `getProfiler()` -> `Profiler.get()`. Day time uses world clocks.
- `SavedData`: `storage.computeIfAbsent(new SavedDataType<>(id, ctor, codec, dataFix))`; ResourcefulLib `SaveHandler` is gone
  (local replacement in `common/handlers/base/SaveHandler`). `CompoundTag` getters return Optional / `getXOr`.
- `ChunkPos` is a record (`x()`, `z()`, `ChunkPos.containing(pos)`, `pack()`/`unpack`); `addRegionTicket` -> `addTicketWithRadius`.
- Registries: `get(id)` -> `getValue`/`getOptional`, `holders()` -> `listElements()`, `registryOrThrow` -> `lookupOrThrow`.
- `getMinBuildHeight` -> `getMinY`, `getMaxBuildHeight` -> `getMaxY() + 1`; `BlockPos.getCenter` -> `Vec3.atCenterOf`.
- `displayClientMessage(m, true)` -> `sendOverlayMessage(m)`; commands use `Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)`.
- `ResourceKey.location()` -> `identifier()`; `InteractionResult.sidedSuccess` gone; `ItemTags.DYEABLE` gone.
- Dyed blocks/items are `ColorCollection`s: `Blocks.WOOL.white()`, `Blocks.DYED_TERRACOTTA.x()`, `Items.DYE.red()`.
- `StructureProcessor` is an interface with `codec()`; `STRUCTURE_PROCESSOR` registry holds `MapCodec`s (no `StructureProcessorType<T>`).
- `SimpleJsonResourceReloadListener<T>(Codec<T>, FileToIdConverter.json(dir))`; `JigsawStructure.MaxDistance`.
- `ResolvableProfile` is a sealed class: `partialProfile().id()`; `GameProfile` is a record.
- `Screen.hasShiftDown()` -> `Minecraft.getInstance().hasShiftDown()`. `FarmBlock` -> `FarmlandBlock`.
- Fabric: `ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id, l)`; `FabricElytraItem` -> `EntityElytraEvents.CUSTOM`;
  `FuelRegistry` gone (`level.fuelValues()`). ResourcefulLib `ExtraByteCodecs.RESOURCE_LOCATION` -> `IDENTIFIER`.
- Client rendering (entities/BERs): extract/submit split. `EntityRenderer.submit(S, PoseStack, SubmitNodeCollector,
  CameraRenderState)`, `createRenderState`/`extractRenderState(entity, state, partialTicks)`; models are
  `EntityModel<S extends EntityRenderState>`. `BlockEntityRenderer<T, S extends BlockEntityRenderState>` with
  `extractRenderState(be, state, partialTicks, cameraPos, crumbling)` (call `BlockEntityRenderer.super`); providers
  get a `BlockEntityRendererProvider.Context` (`itemModelResolver()`, `entityModelSet()`, `playerSkinRenderCache()`).
  Draw with `collector.submitModel/submitModelPart/submitBlockModel/submitCustomGeometry/submitText`. `RenderType.x()`
  -> `RenderTypes.x()` (`net.minecraft.client.renderer.rendertype`). `ModelPart.copyFrom(p)` -> `loadPose(p.storePose())`.
  `CloseablePoseStack` is gone (push/pop). `LightTexture.FULL_BRIGHT` -> `LightCoordsUtil.FULL_BRIGHT`.
- `BlockEntityWithoutLevelRenderer` is gone: item JSON `minecraft:special` + `SpecialModelRenderer.Unbaked` registered via
  `SpecialModelRenderers.ID_MAPPER.put(id, mapCodec)`. Extra block models: Fabric `ExtraModelKey` +
  `SimpleUnbakedExtraModel.blockStateModel(id)`; `BakedModel` -> `BlockStateModel` (`collectParts`).
- Block render layers (`BlockRenderLayerMap`, `ItemBlockRenderTypes`) are gone: the chunk layer is chosen per quad from
  sprite transparency. Item properties / item colors moved to item JSON.
- Particles: `TextureSheetParticle` -> `SingleQuadParticle(level, x, y, z, sprite)`, `getLayer()`;
  `ParticleProvider.createParticle(..., RandomSource)`; `ParticleFactoryRegistry` -> `ParticleProviderRegistry`.
- Fabric client: `KeyBindingHelper` -> `KeyMappingHelper.registerKeyMapping`; `KeyMapping` takes a
  `KeyMapping.Category` (`Category.register(id)`, lang key `key.category.<ns>.<path>`); `EntityModelLayerRegistry` ->
  `ModelLayerRegistry`; `HudRenderCallback` -> `HudElementRegistry.addLast(id, HudElement)`; `WorldRenderEvents` ->
  `LevelRenderEvents` (`COLLECT_SUBMITS` gives poseStack + submitNodeCollector); `ResourceManagerHelper` ->
  `ResourceLoader.get(PackType).registerReloadListener(id, listener)`; `DimensionRenderingRegistry` removed.
  `SimpleJsonResourceReloadListener<T>(codec, FileToIdConverter.json(dir))`. `SimpleWeightedRandomList` -> `WeightedList`.
- `Player.displayClientMessage(msg, true)` -> `sendOverlayMessage(msg)`. `LevelRenderer` no longer has `ticks` or the
  rain/cloud code (moved to `WeatherEffectRenderer`/`CloudRenderer`). First-person arm: `AvatarRenderer.renderHand`,
  `ItemInHandRenderer.submitArmWithItem`. Fluid sprite: `ModelManager.getFluidStateModelSet().get(state).stillMaterial().sprite()`.
- Old GL state -> `RenderPipeline` fields: polygon offset is the bias in `DepthStencilState(op, write, scale, const)`
  (sign flipped, reverse-Z: `polygonOffset(-3,-3)` -> `3, 3`). See the area overlay in `OverlayRenderer`.

## Rules for parallel work
- Only edit files inside your assigned area. If an error in your files is caused by a file outside your area,
  leave a TODO note in your final report instead of editing the other file.
- Prefer the smallest faithful port: keep behavior, do not redesign. If a feature cannot be ported reasonably
  (e.g. an API removed with no replacement), stub it with a `// TODO(port): ...` comment and report it.
- Commit your work in your worktree with a clear message when done.

## Resource format changes (26.2)
- Recipe ingredients are plain strings: `"minecraft:stick"`, `"#minecraft:planks"`, or a list of those. Results stay `{"id", "count"}`.
- `minecraft:dyeable` item tag is gone: dyeing uses per-item `minecraft:crafting_dye` recipes (`dye`, `target`, `result`),
  cauldron washing uses `#minecraft:cauldron_can_remove_dye`. Items still need the dyed-color component support in Java.
- Advancement `display.background` is a texture id without `textures/` and `.png` (`ad_astra:block/steel_panel`).
- dimension_type: `effects`, `bed_works`, `has_raids`, `natural`, `piglin_safe`, `respawn_anchor_works`, `ultrawarm`,
  `fixed_time` removed. New required `has_ender_dragon_fight`; behaviour moved to `attributes`
  (`minecraft:gameplay/bed_rule`, `.../can_start_raid`, `.../respawn_anchor_works`, `.../piglins_zombify`,
  `minecraft:audio/background_music`, ...), plus `default_clock` / `timelines` (day cycle) and `skybox`.
  Custom sky rendering must be keyed on the dimension id in Java (no more `effects` id).
  Fabric has no DimensionRenderingRegistry anymore: `SkyRendererMixin` swaps vanilla sun/moon/stars for the
  planet renderer's renderables (`ModSkyRenderer`; own colored-stars and alpha-blended celestial pipelines), and
  overrides the sunrise fan colour/angle from `sunrise_color`/`sunrise_angle`. `CloudRendererMixin` swaps in
  venus_clouds.png cell data in acid rain biomes. Orbit "no fog" is the orbit biome's black `fog_color`.
  Clouds only render when `minecraft:visual/cloud_color` has alpha > 0 (set on venus/glacio = old `custom_clouds: false`);
  Venus thick fog is `minecraft:visual/fog_end_distance`. Acid rain: `WeatherEffectRendererMixin` (texture) +
  `ClientLevelMixin` (splash particle).
- Biome `effects` now only holds `water_color`/`foliage_color`/`grass_color`/`dry_foliage_color` (hex strings).
  Sky/fog/water fog colors, mood sound, particles, music moved to biome `attributes` (`minecraft:visual/sky_color`,
  `minecraft:visual/fog_color`, `minecraft:visual/water_fog_color`, `minecraft:audio/ambient_sounds`,
  `minecraft:visual/ambient_particles`). `carvers` is a plain list (no `air` key).
- Noise router: `initial_density_without_jaggedness` replaced by `preliminary_surface_level`, a
  `minecraft:find_top_surface` {density, upper_bound, lower_bound, cell_height} (or a constant).
- Item model definitions `assets/<ns>/items/<id>.json`. Model `overrides` are gone (use `select`/`condition`),
  `builtin/entity` parent is gone (use `minecraft:special` with a registered SpecialModelRenderer type),
  `minecraft:item/template_spawn_egg` is gone (each egg needs its own texture). Tints are declared in the
  item definition (`minecraft:dye`).
- Armor textures: `assets/<ns>/equipment/<asset>.json` with layers referencing
  `textures/entity/equipment/<layer>/<texture>.png`.
