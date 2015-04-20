package com.elvishjerricco.cctransport.blocks;

import com.elvishjerricco.cctransport.CCTransport;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockSerialChest extends BlockContainer {
    public IIcon top;
    public IIcon bottom;
    public IIcon sides;


    public BlockSerialChest() {
        super(Material.rock);
        setBlockName("serialChest");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        top = register.registerIcon("cctransport:serialChestTop");
        bottom = register.registerIcon("cctransport:bottom");
        sides = register.registerIcon("cctransport:sides");
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

    @Override
    public TileEntity createNewTileEntity(World w, int meta) {
        return new SerialChestTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int i1, float f1, float f2, float f3) {
        TileEntity te = world.getTileEntity(i, j, k);

        if (te == null || !(te instanceof SerialChestTileEntity)) {
            return true;
        }

        if (world.isRemote) {
            return true;
        }

        player.openGui(CCTransport.instance, 0, world, i, j, k);
        return true;
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, Block block, int i2) {
        SerialChestTileEntity chest = (SerialChestTileEntity) world.getTileEntity(i, j, k);
        if (chest != null) {
            int xCoord = chest.xCoord;
            int yCoord = chest.yCoord;
            int zCoord = chest.zCoord;
            Random random = new Random();
            for (int l = 0; l < chest.getSizeInventory(); l++) {
                ItemStack itemstack = chest.getStackInSlot(l);
                if (itemstack == null) {
                    continue;
                }
                float f = random.nextFloat() * 0.8F + 0.1F;
                float f1 = random.nextFloat() * 0.8F + 0.1F;
                float f2 = random.nextFloat() * 0.8F + 0.1F;
                while (itemstack.stackSize > 0) {
                    int i1 = random.nextInt(21) + 10;
                    if (i1 > itemstack.stackSize)
                    {
                        i1 = itemstack.stackSize;
                    }
                    itemstack.stackSize -= i1;
                    EntityItem entityitem = new EntityItem(world, (float) xCoord + f, (float) yCoord + (1) + f1, (float) zCoord + f2,
                            new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage()));
                    float f3 = 0.05F;
                    entityitem.motionX = (float) random.nextGaussian() * f3;
                    entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
                    entityitem.motionZ = (float) random.nextGaussian() * f3;
                    if (itemstack.hasTagCompound())
                    {
                        entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                    }
                    world.spawnEntityInWorld(entityitem);
                }
            }
        }
        super.breakBlock(world, i, j, k, block, i2);
    }
}
