package dev.amble.timelordregen.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.timelordregen.RegenerationMod;
import dev.amble.timelordregen.api.RegenerationEvents;
import dev.amble.timelordregen.api.RegenerationInfo;
import dev.amble.timelordregen.client.sound.PlayerFollowingLoopingSound;
import dev.amble.timelordregen.core.RegenerationSounds;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class DelayOverlay implements HudRenderCallback {
	private static final Identifier TEXTURE = RegenerationMod.id("textures/gui/delay_overlay.png");
	private static PlayerFollowingLoopingSound SOUND;
	private static final float FADEOUT_THRESHOLD = 0.75F;
	private static final float BACKGROUND_VALUE = 0.1F;

	@Override
	public void onHudRender(DrawContext context, float tickDelta) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.player == null || mc.world == null)
			return;

		RegenerationInfo info = RegenerationInfo.get(mc.player);

		boolean hasFx = info != null && !info.isRegenerating() && info.getDelay().isRunning();
		if (!hasFx) {
			if (SOUND != null) {
				mc.getSoundManager().stop(SOUND);
				SOUND = null;
			}
			return;
		}

		float opacity = info.getDelay().getEventProgress(mc.player.age + tickDelta) + BACKGROUND_VALUE;
		opacity = (float) (opacity * getFadeoutMultiplier(opacity, FADEOUT_THRESHOLD, 1.0F, 12.0F));

		opacity = Math.max(opacity, BACKGROUND_VALUE);

		if (SOUND == null || !mc.getSoundManager().isPlaying(SOUND)) {
			SOUND = new PlayerFollowingLoopingSound(RegenerationSounds.SWING_REGEN_LOOP, SoundCategory.PLAYERS, opacity);
			mc.getSoundManager().play(SOUND);
		}

		SOUND.setVolume(opacity * 0.5F);

		if (opacity <= 0.0F) {
			return;
		}

		// TODO \/ breaks with chat open
		if (mc.options.getPerspective() == Perspective.FIRST_PERSON) {
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			context.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
			context.drawTexture(TEXTURE, 0, 0, 0, 0.0F, 0.0F, context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight());
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private static double getFadeoutMultiplier(float x, float start, float end, float steepness) {
		double t = (x - start) / (end - start);
		// Logistic function centered at t = 0.5
		return 1.0 / (1.0 + Math.exp(steepness * (t - 0.5)));
	}
}
