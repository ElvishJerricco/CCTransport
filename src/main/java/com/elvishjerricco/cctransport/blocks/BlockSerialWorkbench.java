package com.elvishjerricco.cctransport.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSerialWorkbench extends Block {
    public IIcon top;
    public IIcon bottom;
    public IIcon sides;


    public BlockSerialWorkbench() {
        super(Material.rock);
        setBlockName("serialWorkbench");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        top = register.registerIcon("cctransport:serialWorkbenchTop");
        bottom = register.registerIcon("cctransport:serialWorkbenchBottom");
        sides = register.registerIcon("cctransport:serialWorkbenchSides");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        switch (dir) {
        case UP:
            return top;
        case DOWN:
            return bottom;
        default:
            return sides;
        }
    }
}
