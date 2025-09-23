package com.amble.timelordregen.core.particle_effects;

import com.amble.timelordregen.RegenerationMod;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.world.World;

public class RegenParticleEffect implements ParticleEffect {
    private final int entityId;
    private final float yawOffset;
    private final float pitchOffset;
    private final boolean shouldPitch;

    public RegenParticleEffect(int entityId, float yawOffset, float pitchOffset, boolean shouldPitch) {
        this.entityId = entityId;
        this.yawOffset = yawOffset;
        this.pitchOffset = pitchOffset;
        this.shouldPitch = shouldPitch;
    }

    public RegenParticleEffect() {
        this.entityId = -1;
        this.yawOffset = 0;
        this.pitchOffset = 0;
        this.shouldPitch = true;
    }

    public static final Factory<RegenParticleEffect> PARAMETERS_FACTORY = new Factory<>() {
        @Override
        public RegenParticleEffect read(ParticleType<RegenParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            int entityId = stringReader.readInt();
            float yawOffset = stringReader.readFloat();
            float pitchOffset = stringReader.readFloat();
            boolean shouldPitch = stringReader.readBoolean();
            return new RegenParticleEffect(entityId, yawOffset, pitchOffset, shouldPitch);
        }

        @Override
        public RegenParticleEffect read(ParticleType<RegenParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            int entityId = packetByteBuf.readInt();
            float yawOffset = packetByteBuf.readFloat();
            float pitchOffset = packetByteBuf.readFloat();
            boolean shouldPitch = packetByteBuf.readBoolean();
            return new RegenParticleEffect(entityId, yawOffset, pitchOffset, shouldPitch);
        }
    };

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(yawOffset);
        buf.writeFloat(pitchOffset);
        buf.writeBoolean(shouldPitch);
    }

    @Override
    public String asString() {
        return entityId + " " + yawOffset + " " + pitchOffset + " " + shouldPitch;
    }

    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }

    public ParticleType<RegenParticleEffect> getType() {
        return RegenerationMod.RIGHT_REGEN_PARTICLE;
    }

    public float getYawOffset() {
        return this.yawOffset;
    }

    public float getPitchOffset() {
        return this.pitchOffset;
    }

    public boolean getShouldPitch() {
        return this.shouldPitch;
    }
}