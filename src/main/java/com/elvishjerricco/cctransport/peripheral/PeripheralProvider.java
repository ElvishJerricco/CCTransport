package com.elvishjerricco.cctransport.peripheral;

import com.elvishjerricco.cctransport.blocks.BlockSerialChest;
import com.elvishjerricco.cctransport.blocks.BlockSerialWorkbench;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class PeripheralProvider implements IPeripheralProvider {
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        if (block instanceof BlockSerialChest) {
            SerialChestTileEntity tile = (SerialChestTileEntity) world.getTileEntity(x, y, z);;
            if (tile.peripheral == null) {
                tile.peripheral = new SerialChestPeripheral(tile, tile.getWorldObj());
            }
            return tile.peripheral;
        } else if (block instanceof BlockSerialWorkbench) {
            return new SerialWorkBenchPeripheral(world);
        }
        return null;
    }
}
