package net.daplumer.data_modification_utils.PlayerData;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public enum PlayerDataCallbacks {
    STORE_DATA,
    LOAD_DATA;

    public void addCallback(BiConsumer<NbtCompound, PlayerEntity> callback){
        this.callbacks.add(callback);
    }

    private final Set<BiConsumer<NbtCompound, PlayerEntity>> callbacks = new HashSet<>();

    public Set<BiConsumer<NbtCompound, PlayerEntity>> getCallbacks() {
        return callbacks;
    }
}
