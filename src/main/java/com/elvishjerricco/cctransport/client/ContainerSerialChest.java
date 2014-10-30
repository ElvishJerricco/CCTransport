package com.elvishjerricco.cctransport.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSerialChest extends Container {
    public final IInventory playerInventory;
    public final IInventory chestInventory;

    public ContainerSerialChest(IInventory playerInventory, IInventory chestInventory) {
        this.playerInventory = playerInventory;
        this.chestInventory = chestInventory;
        layout();
    }

    public void layout() {
        addSlotToContainer(new Slot(chestInventory, 0, 80, 8));

        int leftCol = 8;
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                addSlotToContainer(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, 102 - (4 - playerInvRow) * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            addSlotToContainer(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, 88));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < 1) {
                if (!mergeItemStack(itemstack1, 1, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(itemstack1, 0, 1, false)) {
                return null;
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
