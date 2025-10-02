package dev.amble.timelordregen.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.timelordregen.RegenerationMod;
import dev.amble.timelordregen.api.RegenerationInfo;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DelayOverlay implements HudRenderCallback {
	private static final Identifier TEXTURE = RegenerationMod.id("textures/gui/delay_overlay.png");

	@Override
	public void onHudRender(DrawContext context, float tickDelta) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.player == null || mc.world == null)
			return;

		RegenerationInfo info = RegenerationInfo.get(mc.player);
		if (info == null) return;

		float opacity = info.getDelay().getEventProgress(mc.player.age + tickDelta); // todo regeneration info is not synced to client

		if (opacity <= 0.0F) return;

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		context.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
		context.drawTexture(TEXTURE, (context.getScaledWindowWidth() / 2) - 8,
				(context.getScaledWindowHeight() / 2) - 8, 0, 0.0F, 0.0F, 16, 16, 16, 16);
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
