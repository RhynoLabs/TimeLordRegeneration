package dev.amble.timelordregen.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.timelordregen.RegenerationMod;
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

	@Override
	public void onHudRender(DrawContext context, float tickDelta) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.player == null || mc.world == null)
			return;

		RegenerationInfo info = RegenerationInfo.get(mc.player);
		if (info == null) return;

		float opacity = info.isRegenerating() ? 0 : info.getDelay().getEventProgress(mc.player.age + tickDelta);

		if (opacity <= 0.0F) {
			if (SOUND != null) {
				mc.getSoundManager().stop(SOUND);
				SOUND = null;
			}

			return;
		}

		if (opacity < FADEOUT_THRESHOLD) {
		} else if (opacity < 1.0F) {
			opacity = (1.0F - opacity) / 0.1F;
		} else {
			opacity = 0.0F;
		}

		if (SOUND == null) {
			SOUND = new PlayerFollowingLoopingSound(RegenerationSounds.SWING_REGEN_LOOP, SoundCategory.PLAYERS, opacity);
			mc.getSoundManager().play(SOUND);
		}

		SOUND.setVolume(opacity);

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
}
