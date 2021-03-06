package com.elvishjerricco.cctransport;

import com.elvishjerricco.cctransport.blocks.BlockSerialChest;
import com.elvishjerricco.cctransport.blocks.BlockSerialWorkbench;
import com.elvishjerricco.cctransport.common.CommonProxy;
import com.elvishjerricco.cctransport.peripheral.PeripheralProvider;
import com.elvishjerricco.cctransport.peripheral.converter.ConversionFactory;
import com.elvishjerricco.cctransport.peripheral.converter.ItemStackConverter;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import com.elvishjerricco.cctransport.turtle.SerialChestUpgrade;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@Mod(modid = CCTransport.MODID, dependencies = "required-after:ComputerCraft")
public class CCTransport {
    public static final String MODID = "cctransport";

    public static final BlockSerialChest serialChest = new BlockSerialChest();
    public static final BlockSerialWorkbench serialWorkbench = new BlockSerialWorkbench();

    @Mod.Instance
    public static CCTransport instance;

    @SidedProxy(clientSide = "com.elvishjerricco.cctransport.client.ClientProxy", serverSide = "com.elvishjerricco.cctransport.common.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Register
        GameRegistry.registerBlock(serialChest, "serialChest");
        GameRegistry.registerTileEntity(SerialChestTileEntity.class, "serialChestTile");

        GameRegistry.registerBlock(serialWorkbench, "serialWorkbench");

        // Creative tab
        try {
            Class cls = Class.forName("dan200.computercraft.ComputerCraft");
            CreativeTabs ccTab = (CreativeTabs) cls.getField("mainCreativeTab").get(null);
            CCTransport.serialChest.setCreativeTab(ccTab);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // Recipe
        Block cc_blockCable = GameRegistry.findBlock("ComputerCraft", "CC-Cable");
        ItemStack cc_modem = new ItemStack(cc_blockCable, 1, 1);

        GameRegistry.addRecipe(new ItemStack(serialChest, 1),
                " M ",
                "MCM",
                " M ",
                'M', cc_modem, 'C', Blocks.chest
        );

        GameRegistry.addRecipe(new ItemStack(serialWorkbench, 1),
                " M ",
                "MCM",
                " M ",
                'M', cc_modem, 'C', Blocks.crafting_table
        );

        // GUI
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        // CC
        ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
        ComputerCraftAPI.registerTurtleUpgrade(new SerialChestUpgrade());

        // CT
        ConversionFactory.registerConverter(new ItemStackConverter(), ItemStack.class);
    }
}
