package com.elvishjerricco.cctransport;

import com.elvishjerricco.cctransport.peripheral.converter.ConversionFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokenManager extends WorldSavedData {
    public static final String IDENTIFIER = "cctransport_tokens";

    private final Random rand;
    private HashMap<String, ItemStack> usedTokens = new HashMap<String, ItemStack>();

    public TokenManager() {
        this(IDENTIFIER);
    }

    public TokenManager(String identifier) {
        super(identifier);
        this.rand = new SecureRandom();
    }

    public static TokenManager tokenManager(World world) {
        TokenManager tokenManager = (TokenManager)world.loadItemData(TokenManager.class, IDENTIFIER);
        if (tokenManager == null) {
            tokenManager = new TokenManager();
            world.setItemData(IDENTIFIER, tokenManager);
        }
        return tokenManager;
    }

    public String getToken(ItemStack itemstack) {
        String token;
        do {
            token = RandomStringUtils.random(32, 0, 0, true, true, null, rand);
        } while(usedTokens.containsKey(token));
        usedTokens.put(token, itemstack);
        markDirty();
        return token;
    }

    public ItemStack getItemStack(String token) {
        return usedTokens.get(token);
    }

    public Map getItemDetails(String token) {
        ItemStack itemstack = getItemStack(token);

        Map itemDetails = (Map) ConversionFactory.convert(itemstack);

        return itemDetails;
    }

    public ItemStack consumeToken(String token) {
        if (usedTokens.containsKey(token)) {
            ItemStack itemstack = usedTokens.get(token);
            usedTokens.remove(token);
            markDirty();
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        int numTokens = nbt.getInteger("numTokens");

        NBTTagList tokens = (NBTTagList)nbt.getTag("tokens");
        NBTTagList items = (NBTTagList)nbt.getTag("items");

        for (int i = 0; i < numTokens; ++i) {
            NBTTagCompound itemNBT = items.getCompoundTagAt(i);
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(itemNBT);
            if (itemstack != null) {
                String token = tokens.getStringTagAt(i);
                usedTokens.put(token, itemstack);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList tokens = new NBTTagList();
        for (String token : usedTokens.keySet()) {
            tokens.appendTag(new NBTTagString(token));
        }

        NBTTagList items = new NBTTagList();
        for (ItemStack item : usedTokens.values()) {
            NBTTagCompound itemNBT = new NBTTagCompound();
            item.writeToNBT(itemNBT);
            items.appendTag(itemNBT);
        }

        nbt.setInteger("numTokens", usedTokens.size());
        nbt.setTag("tokens", tokens);
        nbt.setTag("items", items);
    }
}
