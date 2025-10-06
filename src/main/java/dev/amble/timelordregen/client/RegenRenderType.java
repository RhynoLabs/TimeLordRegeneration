package dev.amble.timelordregen.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.amble.lib.animation.AnimatedEntity;
import dev.amble.lib.animation.AnimatedInstance;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.timelordregen.RegenerationMod;
import dev.amble.timelordregen.animation.AnimationTemplate;
import dev.amble.timelordregen.api.RegenerationCapable;
import dev.amble.timelordregen.api.RegenerationInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public enum RegenRenderType {
	NEW {
		@Override
		public void render(AnimatedInstance entity, float progress, @Nullable BedrockAnimation animation, RegenerationInfo info, Model model, MatrixStack matrices, VertexConsumerProvider provider, float light, @Nullable Arm firstPersonArm) {
			VertexConsumer vertices = provider.getBuffer(RenderLayer.getLightning());

			if (model instanceof ModelWithArms armed) {
				for (Arm arm : Arm.values()) {
					if (firstPersonArm != null && arm != firstPersonArm) continue;

					matrices.push();
					armed.setArmAngle(arm, matrices);
					renderVertex(matrices, vertices, 0.92F, 0.5F, 0.2F, 0.5f, progress);
					matrices.pop();
				}
			}

			if (model instanceof ModelWithHead head && info.isRegenerating() && firstPersonArm == null) {
				matrices.push();
				ModelPart modelPart = head.getHead();
				modelPart.rotate(matrices);
				HeadFeatureRenderer.translate(matrices, false);
				renderVertex(matrices, vertices, 0.92F, 0.5F, 0.2F, 0.5f, progress);
				matrices.pop();
			}
		}

		public void renderVertex(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, float progress) {
			// todo LOQOR DO SOMETHING
			for (int i = 0; i < 10; i++) {
				matrices.multiply(RotationAxis.POSITIVE_Y.rotation(progress * 4 + i * 45));
				matrices.scale(1.0f, 1.0f, 0.65f);
				vertices.vertex(matrices.peek().getPositionMatrix(), 0.0F, 0.0F, 0.0F).color(red, green, blue, alpha).next();
				vertices.vertex(matrices.peek().getPositionMatrix(), -0.266F, 1F, -0.5F * 1F).color(red, green, blue, alpha).next();
				vertices.vertex(matrices.peek().getPositionMatrix(), 0.266F * 1F, 1F, -0.5F * 1F).color(red, green, blue, alpha).next();
				vertices.vertex(matrices.peek().getPositionMatrix(), 0.0F, 0.72F, 1F).color(red, green, blue, alpha).next();
				vertices.vertex(matrices.peek().getPositionMatrix(), (float) (-0.266D * 0.7), 1F, -0.5F * 1F).color(red, green, blue, alpha).next();
			}
		}
	};

	public static final String KEY = "regen_effect";

	public static final Codec<RegenRenderType> CODEC = Codecs.NON_EMPTY_STRING.flatXmap(s -> {
		try {
			return DataResult.success(RegenRenderType.valueOf(s.toUpperCase()));
		} catch (Exception e) {
			return DataResult.error(() -> "Invalid regeneration render type: " + s + "! | " + e.getMessage());
		}
	}, var -> DataResult.success(var.toString()));

	public abstract void render(AnimatedInstance entity, float progress, @Nullable BedrockAnimation animation, RegenerationInfo info, Model model, MatrixStack matrices, VertexConsumerProvider provider, float light, @Nullable Arm firstPersonArm);

	public static void tryRender(AnimatedInstance entity, float progress, Model model, MatrixStack matrices, VertexConsumerProvider provider, float light, @Nullable Arm firstPersonArm) {
		if (!(entity instanceof RegenerationCapable capable)) return;

		capable.withInfo().ifPresent(info -> {
			if (!info.isActive()) return;

			RegenRenderType type = RegenRenderType.NEW;

			BedrockAnimation animation;
			try {
				animation = info.getAnimation().get(AnimationTemplate.Stage.START).reference().get().orElseThrow();

				if (animation.metadata.excess().has(KEY)) {
					String key = animation.metadata.excess().get(KEY).getAsString();
					type = RegenRenderType.valueOf(key.toUpperCase());
				}
			} catch (Exception e) {
				String validOptions = java.util.Arrays.toString(RegenRenderType.values());
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
