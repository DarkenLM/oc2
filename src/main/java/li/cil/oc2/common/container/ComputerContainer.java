package li.cil.oc2.common.container;

import li.cil.oc2.api.bus.device.DeviceTypes;
import li.cil.oc2.common.block.Blocks;
import li.cil.oc2.common.tileentity.ComputerTileEntity;
import li.cil.oc2.common.vm.VirtualMachineItemStackHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public final class ComputerContainer extends AbstractContainer {
    @Nullable
    public static ComputerContainer create(final int id, final PlayerInventory inventory, final PacketBuffer data) {
        final BlockPos pos = data.readBlockPos();
        final TileEntity tileEntity = inventory.player.getEntityWorld().getTileEntity(pos);
        if (!(tileEntity instanceof ComputerTileEntity)) {
            return null;
        }
        return new ComputerContainer(id, (ComputerTileEntity) tileEntity, inventory);
    }

    ///////////////////////////////////////////////////////////////////

    private final ComputerTileEntity computer;

    ///////////////////////////////////////////////////////////////////

    public ComputerContainer(final int id, final ComputerTileEntity computer, final PlayerInventory inventory) {
        super(Containers.COMPUTER_CONTAINER.get(), id);
        this.computer = computer;

        final VirtualMachineItemStackHandlers handlers = computer.getItemStackHandlers();

        handlers.getItemHandler(DeviceTypes.FLASH_MEMORY).ifPresent(itemHandler -> {
            if (itemHandler.getSlots() > 0) {
                this.addSlot(new TypedSlotItemHandler(itemHandler, DeviceTypes.FLASH_MEMORY, 0, 64, 78));
            }
        });

        handlers.getItemHandler(DeviceTypes.MEMORY).ifPresent(itemHandler -> {
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                this.addSlot(new TypedSlotItemHandler(itemHandler, DeviceTypes.MEMORY, slot, 64 + slot * SLOT_SIZE, 24));
            }
        });

        handlers.getItemHandler(DeviceTypes.HARD_DRIVE).ifPresent(itemHandler -> {
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                this.addSlot(new TypedSlotItemHandler(itemHandler, DeviceTypes.HARD_DRIVE, slot, 100 + (slot % 2) * SLOT_SIZE, 60 + (slot / 2) * SLOT_SIZE));
            }
        });

        handlers.getItemHandler(DeviceTypes.CARD).ifPresent(itemHandler -> {
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                this.addSlot(new TypedSlotItemHandler(itemHandler, DeviceTypes.CARD, slot, 38, 24 + slot * SLOT_SIZE));
            }
        });

        createPlayerInventoryAndHotbarSlots(inventory, 8, 115);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public boolean canInteractWith(final PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(computer.getWorld(), computer.getPos()), player, Blocks.COMPUTER_BLOCK.get());
    }
}
