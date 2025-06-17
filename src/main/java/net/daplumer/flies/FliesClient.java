package net.daplumer.flies;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.daplumer.flies.entity.FlyModel;
import net.daplumer.flies.entity.FlyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.renderer.v1.render.RenderLayerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class FliesClient implements ClientModInitializer {
    public static final EntityModelLayer FLY_LAYER = new EntityModelLayer(Identifier.of(Flies.MOD_ID, "fly"), "main");
    /**
     * Runs the mod initializer on the client environment.
     */
    public static final Identifier TEXTURE = Identifier.of(Flies.MOD_ID,"textures/entity/equipment/wings/fly.png");
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                Flies.FLY_TRAP_MAW,
                Flies.FLY_TRAP_STEM
        );
        EntityRendererRegistry.register(Flies.FLY, FlyRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(FLY_LAYER, FlyModel::getTexturedModelData);
        ClientLifecycleEvents.CLIENT_STARTED.register(
                (client -> {
                    //client.getTextureManager().registerTexture(TEXTURE);
                    try {
                        ((ResourceTexture) client.getTextureManager().getTexture(TEXTURE)).loadContents(client.getResourceManager());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(

                (server, resourceManager) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    //client.getTextureManager().registerTexture(TEXTURE);
                    try {
                        ((ResourceTexture) client.getTextureManager().getTexture(TEXTURE)).loadContents(resourceManager);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
