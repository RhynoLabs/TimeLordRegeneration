package dev.amble.timelordregen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.amble.lib.animation.AnimatedInstance;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.timelordregen.RegenerationMod;
import dev.amble.timelordregen.animation.AnimationTemplate;
import dev.amble.timelordregen.api.RegenerationCapable;
import dev.amble.timelordregen.api.RegenerationInfo;
import dev.amble.timelordregen.client.particle.RightRegenParticle;
import dev.amble.timelordregen.core.particle_effects.RegenParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public enum RegenRenderers implements RegenRendering {
	PARTICLE {
		@Override
		public void renderArm(AnimatedInstance entity, float progress, @Nullable BedrockAnimation animation, RegenerationInfo info, Model model, MatrixStack matrices, VertexConsumerProvider provider, float light, Arm arm) {
			matrices.translate(0, 0.81, 0);
			renderParticles(matrices);
		}

		@Override
		public void renderAtHead(AnimatedInstance entity, float progress, @Nullable BedrockAnimation animation, RegenerationInfo info, Model model, MatrixStack matrices, VertexConsumerProvider provider, float light, ModelPart headPart) {
			matrices.translate(0, 0.5, 0);
			renderParticles(matrices);
		}

		public void renderParticles(MatrixStack matrices) {
			MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();

			// Get the sprite provider from the particle factory
			SpriteProvider spriteProvider =
				RightRegenParticle.Factory.getSpriteProvider();

			if (spriteProvider == null) {
				return;
			}

			// Get vertex consumer for particle rendering
			RenderSystem.setShader(GameRenderer::getParticleProgram);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			net.minecraft.client.render.Camera camera = client.gameRenderer.getCamera();
			if (!bufferBuilder.isBuilding()) {
				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			}

			matrices.push();

			Quaternionf rotation = new Quaternionf().set(camera.getRotation());

			matrices.scale(0.25F, 0.25F, 0.25F);


			for (int i = 0; i < 5; i++) {
				matrices.push();

				Sprite sprite = spriteProvider.getSprite(RegenerationMod.RANDOM);
				RenderSystem.setShaderTexture(0, sprite.getAtlasId());
				RenderSystem.setShader(GameRenderer::getPositionTexProgram);
				float u1 = sprite.getMinU();
				float v1 = sprite.getMinV();
				float u2 = sprite.getMaxU();
				float v2 = sprite.getMaxV();

				Matrix4f matrix4f = matrices.peek().getPositionMatrix();
				bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, 0.0F).texture(u1, v1).next();
				bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, 0.0F).texture(u1, v2).next();
				bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, 0.0F).texture(u2, v2).next();
				bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, 0.0F).texture(u2, v1).next();

				matrices.pop();
			}

			tessellator.draw();
			matrices.pop();
		}
	};

	public static final String KEY = "regen_effect";

	public static final Codec<RegenRenderers> CODEC = Codecs.NON_EMPTY_STRING.flatXmap(s -> {
		try {
			return DataResult.success(RegenRenderers.valueOf(s.toUpperCase()));
		} catch (Exception e) {
			return DataResult.error(() -> "Invalid regeneration render type: " + s + "! | " + e.getMessage());
		}
	}, var -> DataResult.success(var.toString()));

	public static void tryRender(AnimatedInstance entity, float progress, Model model, MatrixStack matrices, VertexConsumerProvider provider, float light, @Nullable Arm firstPersonArm) {
		if (!(entity instanceof RegenerationCapable capable)) return;

		capable.withInfo().ifPresent(info -> {
			if (!info.isActive()) return;

			RegenRendering type = RegenRenderers.PARTICLE;

			BedrockAnimation animation;
			try {
				animation = info.getAnimation().get(AnimationTemplate.Stage.START).reference().get().orElseThrow();

				if (animation.metadata.excess().has(KEY)) {
					String key = animation.metadata.excess().get(KEY).getAsString();
					type = RegenRenderers.valueOf(key.toUpperCase());
				}
			} catch (Exception e) {
				String validOptions = java.util.Arrays.toString(RegenRenderers.values());
				String errorMsg = "Failed to get regeneration effect type from animation metadata, valid options are: " + validOptions + ". Error: " + e.getMessage();
				RegenerationMod.LOGGER.error(errorMsg, e);
				throw new RuntimeException(errorMsg, e);
			}

			matrices.push();
			type.render(entity, progress, animation, info, model, matrices, provider, light, firstPersonArm);
			matrices.pop();
		});
	}
}
