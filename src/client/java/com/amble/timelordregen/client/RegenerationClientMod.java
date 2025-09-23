package com.amble.timelordregen.client;

import com.amble.timelordregen.RegenerationMod;
import com.amble.timelordregen.client.gui.RegenerationSettingsScreen;
import com.amble.timelordregen.client.particle.RegenHeadParticle;
import com.amble.timelordregen.client.particle.RightRegenParticle;
import com.amble.timelordregen.core.RegenerationModBlocks;
import com.amble.timelordregen.network.Networking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.render.RenderLayer;

public class RegenerationClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        Animations.init();

        // Block render stuff
        blockRenderLayers();

        // Register particles on client side
        registerParticles();

        ClientPlayNetworking.registerGlobalReceiver(Networking.OPEN_GUI_PACKET, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new RegenerationSettingsScreen(client.player));
                }
            });
        });
    }

    public static class Animations {

        public static class Players {
            //public static final Supplier<PlayerAnimationHolder> REGENERATE = AnimationRegistry.instance().register(() -> new PlayerAnimationHolder(new Identifier(RegenerationMod.MOD_ID, "regeneration"), RegenerationAnimations.CASE_OPEN_PLAYER));

            public static void init() {}

        }

        public static void init() {
            Players.init();
        }
    }

    public void blockRenderLayers() {
        BlockRenderLayerMap map = BlockRenderLayerMap.INSTANCE;
        map.putBlock(RegenerationModBlocks.CADON_LEAVES, RenderLayer.getCutout());
        map.putBlock(RegenerationModBlocks.CADON_TRAPDOOR, RenderLayer.getCutout());
        map.putBlock(RegenerationModBlocks.CADON_DOOR, RenderLayer.getCutout());
    }

    public void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(RegenerationMod.RIGHT_REGEN_PARTICLE, RightRegenParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegenerationMod.REGEN_HEAD_PARTICLE, RegenHeadParticle.Factory::new);
    }
}
