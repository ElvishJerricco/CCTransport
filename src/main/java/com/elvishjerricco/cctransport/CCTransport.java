package com.elvishjerricco.cctransport;

import com.elvishjerricco.cctransport.blocks.BlockSerialChest;
import com.elvishjerricco.cctransport.common.CommonProxy;
import com.elvishjerricco.cctransport.peripheral.PeripheralProvider;
import com.elvishjerricco.cctransport.peripheral.converter.ConversionFactory;
import com.elvishjerricco.cctransport.peripheral.converter.ItemStackConverter;
import com.elvishjerricco.cctransport.tiles.SerialChestTileEntity;
import com.elvishjerricco.cctransport.turtle.SerialChestUpgrade;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@Mod(modid = CCTransport.MODID, dependencies = "required-after:ComputerCraft")
public class CCTransport {
    public static final String MODID = "CCTransport";

    public static final BlockSerialChest serialChest = new BlockSerialChest();

    @Mod.Instance
    public static CCTransport instance;

    @SidedProxy(clientSide = "com.elvishjerricco.cctransport.client.ClientProxy", serverSide = "com.elvishjerricco.cctransport.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        // Register
        GameRegistry.registerBlock(serialChest, "serialChest");
        GameRegistry.registerTileEntity(SerialChestTileEntity.class, "serialChestTile");

        // Creative tab
        CreativeTabs ccTab = null;
        for (CreativeTabs tab : CreativeTabs.creativeTabArray) {
            if (tab.getTabLabel().equals("ComputerCraft")) {
                ccTab = tab;
                break;
            }
        }
        serialChest.setCreativeTab(ccTab);

        // Recipe
        Block cc_blockCable = GameRegistry.findBlock("ComputerCraft", "CC-Cable");
        ItemStack cc_modem = new ItemStack(cc_blockCable, 1, 1);

        GameRegistry.addRecipe(new ItemStack(serialChest, 1),
                " M ",
                "MCM",
                " M ",
                'M', cc_modem, 'C', Blocks.chest
        );

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
        ComputerCraftAPI.registerTurtleUpgrade(new SerialChestUpgrade());

        ConversionFactory.registerConverter(new ItemStackConverter(), ItemStack.class);
    }
}
