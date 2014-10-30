package com.elvishjerricco.cctransport.common;

import com.elvishjerricco.cctransport.client.ContainerSerialChest;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof SerialChestTileEntity) {
            SerialChestTileEntity cerealChestTileEntity = (SerialChestTileEntity)te;
            return new ContainerSerialChest(player.inventory, cerealChestTileEntity);
        } else {
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
