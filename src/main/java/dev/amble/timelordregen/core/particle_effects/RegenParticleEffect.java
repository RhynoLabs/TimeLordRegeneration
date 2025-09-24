package dev.amble.timelordregen.core.particle_effects;

import dev.amble.timelordregen.RegenerationMod;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.world.World;

public class RegenParticleEffect implements ParticleEffect {
    private final int entityId;
    @Getter
    private final float yawOffset;
    @Getter
    private final float pitchOffset;
    private final boolean shouldPitch;
    private final boolean shouldFollowPlayer;

    public RegenParticleEffect(int entityId, float yawOffset, float pitchOffset, boolean shouldPitch, boolean shouldFollowPlayer) {
        this.entityId = entityId;
        this.yawOffset = yawOffset;
        this.pitchOffset = pitchOffset;
        this.shouldPitch = shouldPitch;
        this.shouldFollowPlayer = shouldFollowPlayer;
    }

    public RegenParticleEffect() {
        this.entityId = -1;
        this.yawOffset = 0;
        this.pitchOffset = 0;
        this.shouldPitch = true;
        this.shouldFollowPlayer = true;
    }

    public static final Factory<RegenParticleEffect> PARAMETERS_FACTORY = new Factory<>() {
        @Override
        public RegenParticleEffect read(ParticleType<RegenParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            int entityId = stringReader.readInt();
            float yawOffset = stringReader.readFloat();
            float pitchOffset = stringReader.readFloat();
            boolean shouldPitch = stringReader.readBoolean();
            boolean shouldFollowPlayer = stringReader.readBoolean();
            return new RegenParticleEffect(entityId, yawOffset, pitchOffset, shouldPitch, shouldFollowPlayer);
        }

        @Override
        public RegenParticleEffect read(ParticleType<RegenParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            int entityId = packetByteBuf.readInt();
            float yawOffset = packetByteBuf.readFloat();
            float pitchOffset = packetByteBuf.readFloat();
            boolean shouldPitch = packetByteBuf.readBoolean();
            boolean shouldFollowPlayer = packetByteBuf.readBoolean();
            return new RegenParticleEffect(entityId, yawOffset, pitchOffset, shouldPitch, shouldFollowPlayer);
        }
    };

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(yawOffset);
        buf.writeFloat(pitchOffset);
        buf.writeBoolean(shouldPitch);
        buf.writeBoolean(shouldFollowPlayer);
    }

    @Override
    public String asString() {
        return entityId + " " + yawOffset + " " + pitchOffset + " " + shouldPitch + " " + shouldFollowPlayer;
    }

    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }

    public ParticleType<RegenParticleEffect> getType() {
        return RegenerationMod.RIGHT_REGEN_PARTICLE;
    }

    public boolean getShouldPitch() {
        return this.shouldPitch;
    }

    public boolean getShouldFollowPlayer() {
        return this.shouldFollowPlayer;
    }
}