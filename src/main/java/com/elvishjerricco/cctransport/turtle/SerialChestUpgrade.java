package com.elvishjerricco.cctransport.turtle;

import com.elvishjerricco.cctransport.CCTransport;
import com.elvishjerricco.cctransport.peripheral.SerialChestPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SerialChestUpgrade implements ITurtleUpgrade {
    @Override
    public int getUpgradeID() {
        return UpgradeIDs.serialChestUpgradeID;
    }

    @Override
    public String getUnlocalisedAdjective() {
        return "cctransport.serialChestUpgrade";
    }

    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.Peripheral;
    }

    @Override
    public ItemStack getCraftingItem() {
        return new ItemStack(CCTransport.serialChest);
    }

    @Override
    public IPeripheral createPeripheral(final ITurtleAccess turtle, TurtleSide side) {
        return new SerialChestPeripheral(turtle.getInventory(), turtle.getWorld()) {
            @Override
            public int getSlot() {
                return turtle.getSelectedSlot();
            }
        };
    }

    @Override
    public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
        return null;
    }

    @Override
    public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
        return CCTransport.serialChest.top;
    }

    @Override
    public void update(ITurtleAccess turtle, TurtleSide side) {
    }
}
