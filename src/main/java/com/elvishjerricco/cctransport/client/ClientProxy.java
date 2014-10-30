package com.elvishjerricco.cctransport.client;

import com.elvishjerricco.cctransport.common.CommonProxy;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof SerialChestTileEntity) {
            return new GuiCerealChest((ContainerSerialChest) getServerGuiElement(ID, player, world, x, y, z));
        } else {
            return null;
        }
    }
}
