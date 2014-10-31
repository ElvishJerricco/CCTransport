package com.elvishjerricco.cctransport.peripheral;

import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PeripheralProvider implements IPeripheralProvider {
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof SerialChestTileEntity) {
            SerialChestTileEntity tile = (SerialChestTileEntity) te;
            if (tile.peripheral == null) {
                tile.peripheral = new SerialChestPeripheral(tile, tile.getWorldObj());
            }
            return tile.peripheral;
        }
        return null;
    }
}
