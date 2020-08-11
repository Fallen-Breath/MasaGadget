package io.github.plusls.MasaGadget.mixin.server.block;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class MixinShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable {

    @Shadow
    public abstract CompoundTag toTag(CompoundTag tag);

    public MixinShulkerBoxBlockEntity() {
        super(null);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        // 在生成世界时可能会产生空指针
        if (this.world == null) {
            return;
        }
        if (ServerNetworkHandler.lastBlockPosMap.containsValue(this.pos)) {
            ((ServerWorld) this.world).getChunkManager().markForUpdate(this.getPos());
            MasaGadgetMod.LOGGER.debug("update ShulkerBoxBlockEntity: {}", this.pos);
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 0, this.toTag(new CompoundTag()));
    }
}
