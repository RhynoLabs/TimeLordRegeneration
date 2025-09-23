package com.amble.timelordregen.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class RegenHeadParticle extends ExplosionSmokeParticle {
    private final SpriteProvider spriteProvider;

    RegenHeadParticle(ClientWorld clientWorld, double d, double e, double f, double velX, double velY, double velZ, SpriteProvider spriteProvider) {
        // Widen spawn area by randomizing initial position slightly
        super(
                clientWorld,
                d + (Math.random() * 0.4 - 0.2), // X offset
                e + (Math.random() * 0.4 - 0.2), // Y offset
                f + (Math.random() * 0.4 - 0.2), // Z offset
                0, 0, 0, spriteProvider
        );
        this.spriteProvider = spriteProvider;
        this.gravityStrength = 0.01f;
        this.velocityMultiplier = 0.999f;
        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.1000000074505806;
        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.1000000074505806;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.angle = (float) Math.random() * 6.2831855F;
        this.prevAngle = this.angle;
        this.alpha = 1f;
        this.velocityY = this.random.nextFloat() * 0.2F + 0.6F;
        this.scale *= 0.5f;
        this.setColor(1f, 0.9f, 0.9f);
        this.maxAge = this.random.nextInt(10);
        this.collidesWithWorld = true;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    public void tick() {
        super.tick();
        if (!this.dead) {
            this.setSpriteForAge(this.spriteProvider);
        }
        if (!(this.alpha <= 0.0F)) {
            if (this.alpha > 0.01F) {
                this.alpha -= 0.01F;
            }
        } else {
            this.markDead();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            RegenHeadParticle regenParticle = new RegenHeadParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            regenParticle.setSprite(this.spriteProvider);
            return regenParticle;
        }
    }
}
