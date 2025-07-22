package com.rhyno.timelordregen.regeneration;

import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSettings {
    private static final Map<UUID, org.joml.Vector3f> particleColors = new ConcurrentHashMap<>();

    public static Vector3f getParticleColor(UUID uuid) {
        return particleColors.getOrDefault(uuid, new org.joml.Vector3f(1.0f, 0.5f, 0.0f)); // default orange
    }

    public static void setParticleColor(UUID uuid, org.joml.Vector3f color) {
        particleColors.put(uuid, color);
    }
}
