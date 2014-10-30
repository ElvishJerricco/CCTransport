package com.elvishjerricco.cctransport.peripheral;

import com.elvishjerricco.cctransport.TokenManager;
import com.elvishjerricco.cctransport.peripheral.converter.ConversionFactory;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class SerialChestPeripheral implements IPeripheral {
    private final SerialChestTileEntity tile;

    TokenManager tokenManager() {
        return TokenManager.tokenManager(tile.getWorldObj());
    }

    public SerialChestPeripheral(SerialChestTileEntity tile) {
        this.tile = tile;
    }

    // Returns Token, {damage=,count=,name=}
    public Object[] serialize(IComputerAccess computer, ILuaContext context, Object[] arguments) {
        ItemStack itemstack = tile.decrStackSize(0, 1);
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

        ItemStack stack = tile.getStackInSlot(0);
        ItemStack serializedStack = tokenManager().getItemStack(token);
        if (serializedStack == null) {
            return new Object[] {false, "Invalid token"};
        } else if (stack == null) {
            tile.setInventorySlotContents(0, serializedStack);
            tokenManager().consumeToken(token);
            return new Object[] {true};
        } else if (stack.isItemEqual(serializedStack)
                && ItemStack.areItemStackTagsEqual(stack, serializedStack)
                && stack.getMaxStackSize() >= stack.stackSize + serializedStack.stackSize
                && tile.getInventoryStackLimit() >= stack.stackSize + serializedStack.stackSize) {
            stack.stackSize += serializedStack.stackSize;
            tile.setInventorySlotContents(0, stack);
            tokenManager().consumeToken(token);
            return new Object[] {true};
        } else {
            return new Object[] {false, "No room for item"};
        }
    }

    public void queueInventoryChangeEvent() {
        for (IComputerAccess computer : attachedComputers) {
            Map map = (Map) ConversionFactory.convert(tile.getStackInSlot(0));
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
}
