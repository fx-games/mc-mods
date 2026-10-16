package earth.terrarium.adastra.common.blockentities.flag;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.mojang.authlib.GameProfile;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.common.blockentities.flag.content.FlagContent;
import earth.terrarium.adastra.common.blockentities.flag.content.UrlContent;
import earth.terrarium.adastra.common.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FlagBlockEntity extends BlockEntity {

    @Nullable
    private ResolvableProfile owner;

    @Nullable
    private FlagContent content;

    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FLAG.get(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        if (owner != null) {
            tag.store("FlagOwner", ResolvableProfile.CODEC, this.owner);
        }
        if (content != null) {
            tag.store("FlagContent", CompoundTag.CODEC, content.toFullTag());
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.read("FlagOwner", ResolvableProfile.CODEC).ifPresent(this::setOwner);
        tag.getString("FlagUrl").ifPresent(url -> this.content = UrlContent.of("https://imgur.com/" + url));
        tag.read("FlagContent", CompoundTag.CODEC).ifPresent(c -> this.content = FlagContent.fromTag(c));
    }

    @Nullable
    public ResolvableProfile getOwner() {
        return this.owner;
    }

    public void setOwner(GameProfile profile) {
        this.setOwner(ResolvableProfile.createResolved(profile));
    }

    public void setOwner(ResolvableProfile profile) {
        synchronized (this) {
            this.owner = profile;
        }
        this.loadOwnerProperties();
    }

    // TODO(port): unresolved profiles are no longer resolved here (SkullBlockEntity resolver API changed).
    private void loadOwnerProperties() {
        this.setChanged();
    }

    @Nullable
    public FlagContent getContent() {
        return this.content;
    }

    public void setContent(@Nullable FlagContent content) {
        this.content = content;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
