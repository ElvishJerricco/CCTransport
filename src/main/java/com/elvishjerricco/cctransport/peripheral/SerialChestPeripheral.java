package com.elvishjerricco.cctransport.peripheral;

import com.elvishjerricco.cctransport.TokenManager;
import com.elvishjerricco.cctransport.peripheral.converter.ConversionFactory;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class SerialChestPeripheral implements IPeripheral {
    private final IInventory inventory;
    private final TokenManager tokenManager;

    public SerialChestPeripheral(IInventory inventory, World world) {
        this(inventory, TokenManager.tokenManager(world));
    }

    public TokenManager tokenManager() {
        return tokenManager;
    }

    public SerialChestPeripheral(IInventory inventory, TokenManager tokenManager) {
        this.inventory = inventory;
        this.tokenManager = tokenManager;
    }

    // Returns Token, {damage=,count=,name=}
    public Object[] serialize(IComputerAccess computer, ILuaContext context, Object[] arguments) {
        ItemStack itemstack = inventory.decrStackSize(getSlot(), 1);
        if (itemstack == null) {
            return new Object[]{false, "No item to serialize"};
        }

        String token = tokenManager().getToken(itemstack);
        Map itemDetails = tokenManager().getItemDetails(token);

        return new Object[]{token, itemDetails};
    }

    public Object[] deserialize(IComputerAccess computer, ILuaContext context, Object[] arguments) throws LuaException {
        if (!(arguments[0] instanceof String)) {
            throw new LuaException("Expected string");
        }

        String token = (String) arguments[0];

        ItemStack stack = inventory.getStackInSlot(getSlot());
        ItemStack serializedStack = tokenManager().getItemStack(token);
        if (serializedStack == null) {
            return new Object[] {false, "Invalid token"};
        } else if (stack == null) {
            inventory.setInventorySlotContents(getSlot(), serializedStack);
            tokenManager().consumeToken(token);
            return new Object[] {true};
        } else if (stack.isItemEqual(serializedStack)
                && ItemStack.areItemStackTagsEqual(stack, serializedStack)
                && stack.getMaxStackSize() >= stack.stackSize + serializedStack.stackSize
                && inventory.getInventoryStackLimit() >= stack.stackSize + serializedStack.stackSize) {
            stack.stackSize += serializedStack.stackSize;
            inventory.setInventorySlotContents(getSlot(), stack);
            tokenManager().consumeToken(token);
            return new Object[] {true};
        } else {
            return new Object[] {false, "No room for item"};
        }
    }

    public void queueInventoryChangeEvent() {
        for (IComputerAccess computer : attachedComputers) {
            Map map = (Map) ConversionFactory.convert(inventory.getStackInSlot(getSlot()));
            computer.queueEvent("serial_chest_update", new Object[]{computer.getAttachmentName(), map});
        }
    }

    // IPeripheral
    private final String[] methods = new String[]{"serialize", "deserialize"};
    private final ArrayList<IComputerAccess> attachedComputers = new ArrayList<IComputerAccess>();

    @Override
    public String getType() {
        return "serial_chest";
    }

    @Override
    public String[] getMethodNames() {
        return methods;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        if (methods[method].equals("serialize")) {
            return serialize(computer, context, arguments);
        } else if (methods[method].equals("deserialize")) {
            return deserialize(computer, context, arguments);
        } else {
            throw new LuaException("Invalid method");
        }
    }

    @Override
    public void attach(IComputerAccess computer) {
        attachedComputers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        attachedComputers.remove(computer);
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    public int getSlot() {
        return 0;
    }
}
