package com.elvishjerricco.cctransport.tiles;

import com.elvishjerricco.cctransport.peripheral.SerialChestPeripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class SerialChestTileEntity extends TileEntity implements IInventory {
    // TileEntity
    public SerialChestPeripheral peripheral;

    @Override
    public void updateEntity() {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        NBTTagCompound itemTag = new NBTTagCompound();
        if (stack != null) {
            stack.writeToNBT(itemTag);
            nbt.setTag("stack", itemTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("stack")) {
            NBTTagCompound itemTag = nbt.getCompoundTag("stack");
            stack = ItemStack.loadItemStackFromNBT(itemTag);
        }
    }

    // IInventory
    private ItemStack stack;

    @Override
    public void markDirty() {
        super.markDirty();
        if (peripheral != null) {
            peripheral.queueInventoryChangeEvent();
        }
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (stack != null) {
            if (stack.stackSize <= amount) {
                ItemStack itemstack = stack;
                stack = null;
                markDirty();
                return itemstack;
            }
            ItemStack itemstack1 = stack.splitStack(amount);
            if (stack.stackSize == 0) {
                stack = null;
            }
            markDirty();
            return itemstack1;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack i = stack;
        stack = null;
        markDirty();
        return i;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack st) {
        stack = st;
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "serialChest";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack st) {
        return true;
    }
}
