package com.elvishjerricco.cctransport.peripheral.converter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemStackConverter implements ConversionFactory.Converter<ItemStack> {

    @Override
    public Object convert(ItemStack itemstack) {
        if (itemstack == null) {
            return null;
        }

        String name = Item.itemRegistry.getNameForObject(itemstack.getItem());
        int damage = itemstack.getItemDamage();
        int count = itemstack.stackSize;

        Map itemDetails = new HashMap();
        itemDetails.put("name", name);
        itemDetails.put("damage", damage);
        itemDetails.put("count", count);

        return itemDetails;
    }
}
