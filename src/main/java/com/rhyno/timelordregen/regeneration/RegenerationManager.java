package com.rhyno.timelordregen.regeneration;

import com.rhyno.timelordregen.RegenerationMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.util.*;

public class RegenerationManager {

    private static final int MAX_REGENERATIONS = 12;
    private static final int REGEN_DURATION_TICKS = 700; // 35 seconds * 20 ticks
    private static final int PARTICLE_INTERVAL_TICKS = 5;    // How often to spawn particles
    private static final Map<UUID, Integer> regenerationsLeft = new HashMap<>();
    private static final Map<UUID, Boolean> regeneratingPlayers = new HashMap<>();
    private static final Map<UUID, Integer> regenCountdowns = new HashMap<>();

    private static final RegistryKey<DamageType> MAGIC_DAMAGE_TYPE_KEY =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("magic"));

    public static void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Integer>> iterator = regenCountdowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID playerId = entry.getKey();
            int timeLeft = entry.getValue() - 1;

            if (timeLeft <= 0) {
                PlayerEntity player = server.getPlayerManager().getPlayer(playerId);
                if (player != null) {
                    completeRegeneration(player);
                }
                iterator.remove();
            } else {
                entry.setValue(timeLeft);
            }
        }
    }

    public static boolean canRegenerate(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return regenerationsLeft.getOrDefault(uuid, MAX_REGENERATIONS) > 0 && !isRegenerating(player);
    }

    public static void triggerRegeneration(PlayerEntity player) {
        UUID uuid = player.getUuid();
        if (isRegenerating(player)) return;

        regeneratingPlayers.put(uuid, true);
        regenerationsLeft.put(uuid, regenerationsLeft.getOrDefault(uuid, MAX_REGENERATIONS) - 12);

        player.setInvulnerable(true);
        ServerWorld world = (ServerWorld) player.getWorld();

        world.playSound(null, player.getBlockPos(), RegenerationMod.REGEN_SOUND, SoundCategory.PLAYERS, 1f, 1f);
        Vector3f color = PlayerSettings.getParticleColor(player.getUuid());
        world.spawnParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1, player.getZ(),
                50, 0.5, 0.5, 0.5, 0.05
        );
        RegistryEntry<DamageType> magicDamageType = player.getServer()
                .getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(MAGIC_DAMAGE_TYPE_KEY)
                .orElseThrow(() -> new IllegalStateException("Magic damage type not found"));

        for (Entity entity : world.getOtherEntities(player, player.getBoundingBox().expand(5))) {
            entity.damage(new DamageSource(magicDamageType, player), 5f);
        }

        // Start 700 tick (35 sec) countdown
        regenCountdowns.put(uuid, 700);
    }

    private static void completeRegeneration(PlayerEntity player) {
        UUID uuid = player.getUuid();
        ServerWorld world = (ServerWorld) player.getWorld();

        regeneratingPlayers.put(uuid, false);
        player.setInvulnerable(false);
        player.setHealth(player.getMaxHealth());
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 1, player.getZ(), 40, 0.5, 0.5, 0.5, 0.05);
    }

    public static boolean isRegenerating(PlayerEntity player) {
        return regeneratingPlayers.getOrDefault(player.getUuid(), false);
    }

    public static int getRegenerationsLeft(PlayerEntity player) {
        return regenerationsLeft.getOrDefault(player.getUuid(), MAX_REGENERATIONS);
    }
}