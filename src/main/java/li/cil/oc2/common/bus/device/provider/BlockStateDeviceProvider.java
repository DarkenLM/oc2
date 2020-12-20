package li.cil.oc2.common.bus.device.provider;

import li.cil.oc2.api.bus.device.Device;
import li.cil.oc2.api.bus.device.object.Callbacks;
import li.cil.oc2.api.bus.device.object.ObjectDevice;
import li.cil.oc2.api.bus.device.provider.BlockDeviceQuery;
import li.cil.oc2.common.bus.device.provider.util.AbstractBlockDeviceProvider;
import li.cil.oc2.common.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public final class BlockStateDeviceProvider extends AbstractBlockDeviceProvider {
    @Override
    public LazyOptional<Device> getDevice(final BlockDeviceQuery query) {
        final World world = query.getWorld();
        final BlockPos position = query.getQueryPosition();

        final BlockState blockState = world.getBlockState(position);
        final Block block = blockState.getBlock();

        if (block.isAir(blockState, world, position)) {
            return LazyOptional.empty();
        }

        if (!Callbacks.hasMethods(block)) {
            return LazyOptional.empty();
        }

        final String typeName = WorldUtils.getBlockName(world, position);
        return LazyOptional.of(() -> new ObjectDevice(block, typeName));
    }
}
