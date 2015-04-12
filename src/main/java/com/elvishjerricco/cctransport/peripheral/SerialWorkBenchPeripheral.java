package com.elvishjerricco.cctransport.peripheral;

import com.elvishjerricco.cctransport.TokenManager;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class SerialWorkBenchPeripheral extends Container implements IPeripheral {
    private final World worldObj;
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    private IInventory craftResult = new InventoryCraftResult();

    public SerialWorkBenchPeripheral(World worldObj) {
        this.worldObj = worldObj;
    }

    private TokenManager tokenManager() {
        return TokenManager.tokenManager(worldObj);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory matrix) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    public Object[] craft(IComputerAccess computer, ILuaContext context, Object[] arguments) throws LuaException {
        if (arguments.length < 1) {
            throw new LuaException("Expected recipe");
        }
        if (!(arguments[0] instanceof Map)) {
            throw new LuaException("Bad argument");
        }

        Map<Double, Map<Double, String>> matrix = (Map<Double, Map<Double, String>>) arguments[0];

        ArrayList<String> tokens = new ArrayList<String>(craftMatrix.getSizeInventory());
        for (int y = 0; y < 3; ++y) {
            Map<Double, String> line = matrix.get((double)(y + 1));
            if (line != null) {
                for (int x = 0; x < 3; ++x) {
                    String token = line.get((double)(x + 1));
                    if (token != null) {
                        if (tokens.contains(token)) {
                            return new Object[] {false, "Cannot use same token twice"};
                        }
                        ItemStack stack = tokenManager().getItemStack(token);
                        if (stack == null) {
                            return new Object[] {false, "Invalid token: " + token};
                        }
                        int i = x + y * 3;
                        craftMatrix.setInventorySlotContents(i, stack);
                        tokens.set(i, token);
                    }
                }
            }
        }

        if (craftResult.getStackInSlot(0) != null) {
            ArrayList ret = new ArrayList();
            while (craftResult.getStackInSlot(0) != null) {
                ret.add(tokenManager().getToken(craftResult.decrStackSize(0, 1)));
            }

            for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
                ItemStack oldStack = this.craftMatrix.decrStackSize(i, 1);

                if (oldStack != null ) {
                    tokenManager().consumeToken(tokens.get(i));

                    if (oldStack.getItem().hasContainerItem(oldStack)) {
                        ItemStack container = oldStack.getItem().getContainerItem(oldStack);
                        if (container != null && !(container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage())) {
                            ret.add(tokenManager().getToken(container));
                        }
                    }

                    // support tokens with multiple items per stack, which isn't actually implemented anywhere
                    if (craftMatrix.getStackInSlot(i) != null) {
                        ret.add(tokenManager().getToken(craftMatrix.getStackInSlot(i)));
                        craftMatrix.setInventorySlotContents(i, null);
                    }
                }
            }
            return ret.toArray();
        } else {
            return new Object[] {false, "Invalid recipe"};
        }
    }

    @Override
    public String getType() {
        return "serial_workbench";
    }

    private final String[] methods = {"craft"};

    @Override
    public String[] getMethodNames() {
        return methods;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        if (method == 0) {
            return craft(computer, context, arguments);
        }
        return new Object[0];
    }

    @Override
    public void attach(IComputerAccess computer) {
    }

    @Override
    public void detach(IComputerAccess computer) {
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    // Container
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return false;
    }
}
