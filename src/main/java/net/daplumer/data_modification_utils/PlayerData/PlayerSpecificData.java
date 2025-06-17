package net.daplumer.data_modification_utils.PlayerData;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

@SuppressWarnings("unused")
public class PlayerSpecificData<T> {
    private final HashMap<PlayerEntity, T> values = new HashMap<>();
    public PlayerSpecificData(T defaultValue, String key,  Codec<T> codec){
        PlayerDataCallbacks.STORE_DATA.addCallback((nbt,player) -> nbt.put(key,codec,values.get(player)));
        PlayerDataCallbacks.LOAD_DATA.addCallback((nbt, player) -> values.put(player, nbt.get(key,codec).orElse(defaultValue)));
    }
    public T get(PlayerEntity player){
        return values.get(player);
    }
    public void set(PlayerEntity player, T value) {
        values.put(player, value);
    }
}
