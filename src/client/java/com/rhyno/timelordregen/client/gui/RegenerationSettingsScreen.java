package com.rhyno.timelordregen.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import com.rhyno.timelordregen.regeneration.PlayerSettings;
import com.rhyno.timelordregen.regeneration.RegenerationManager;
import org.joml.Vector3f;

public class RegenerationSettingsScreen extends Screen {
    private final PlayerEntity player;
    private ColorSlider redSlider;
    private ColorSlider greenSlider;
    private ColorSlider blueSlider;
    private Vector3f color;

    public RegenerationSettingsScreen(PlayerEntity player) {
        super(Text.literal("Regeneration Settings"));
        this.player = player;
        this.color = PlayerSettings.getParticleColor(player.getUuid());
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        redSlider = new ColorSlider(centerX - 100, centerY - 40, 200, 20, Text.literal("Red"), color.x(), v -> updateColor());
        greenSlider = new ColorSlider(centerX - 100, centerY - 10, 200, 20, Text.literal("Green"), color.y(), v -> updateColor());
        blueSlider = new ColorSlider(centerX - 100, centerY + 20, 200, 20, Text.literal("Blue"), color.z(), v -> updateColor());

        this.addDrawableChild(redSlider);
        this.addDrawableChild(greenSlider);
        this.addDrawableChild(blueSlider);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .position(centerX - 50, centerY + 60)
                .size(100, 20)
                .build()
        );
    }

    private void updateColor() {
        color = new Vector3f(
                redSlider.getSliderValue(),
                greenSlider.getSliderValue(),
                blueSlider.getSliderValue()
        );
        PlayerSettings.setParticleColor(player.getUuid(), color);
    }

    private class ColorSlider extends SliderWidget {
        private final java.util.function.Consumer<Float> onChange;
        private final String label;

        public ColorSlider(int x, int y, int width, int height, Text label, double value, java.util.function.Consumer<Float> onChange) {
            super(x, y, width, height, label, value);
            this.onChange = onChange;
            this.label = label.getString();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal(label + ": " + (int) (this.value * 255)));
        }

        @Override
        protected void applyValue() {
            onChange.accept((float) this.value);
        }

        public float getSliderValue() {
            return (float) this.value;
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        drawCenteredText(drawContext.getMatrices(), this.textRenderer,
                "Remaining Regenerations: " + RegenerationManager.getRegenerationsLeft(player),
                this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        super.render(drawContext, mouseX, mouseY, delta);
    }

    private void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
        Text literalText = Text.literal(text);
        int textWidth = textRenderer.getWidth(literalText);

        textRenderer.draw(
                String.valueOf(literalText),
                x - textWidth / 2f,
                y,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL,
                0,
                15728880,
                false
        );
    }
}