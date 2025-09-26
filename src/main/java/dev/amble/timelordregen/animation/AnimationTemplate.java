package dev.amble.timelordregen.animation;

import dev.amble.lib.skin.client.SkinGrabber;
import dev.amble.timelordregen.RegenerationMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import dev.amble.lib.skin.SkinData;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationTemplate extends EnumMap<AnimationTemplate.Stage, AnimationTemplate.ReferenceWrapper> implements Identifiable {
	// idk where to put this
	public static final Codec<TimeUnit> TIME_UNIT_CODEC = Codecs.NON_EMPTY_STRING.flatXmap(s -> {
		try {
			return DataResult.success(TimeUnit.valueOf(s.toUpperCase()));
		} catch (Exception e) {
			return DataResult.error(() -> "Invalid time unit: " + s + "! | " + e.getMessage());
		}
	}, var -> DataResult.success(var.toString()));

	public static final Codec<AnimationTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Stage.CODEC, ReferenceWrapper.CODEC).fieldOf("stages").forGetter(template -> template),
			SkinChangePoint.CODEC.optionalFieldOf("skin_change").forGetter(AnimationTemplate::getSkinChange)
	).apply(instance, (map, skinChangeOpt) -> {
		AnimationTemplate template = new AnimationTemplate(map);
		skinChangeOpt.ifPresent(skinChange -> template.skinChange = skinChange);
		return template;
	}));

	private Identifier id;
	@Nullable private SkinChangePoint skinChange;

	public AnimationTemplate() {
		super(Stage.class);
	}

	public AnimationTemplate(ReferenceWrapper start, ReferenceWrapper loop, ReferenceWrapper end) {
		this();

		this.put(Stage.START, start);
		this.put(Stage.LOOP, loop);
		this.put(Stage.END, end);
	}

	public AnimationTemplate(Map<Stage, ReferenceWrapper> map) {
		this();

		this.putAll(map);
	}

	public AnimationSet instantiate(boolean skinChange) {
		AnimationSet set = new AnimationSet(this);

		if (skinChange && this.skinChange != null) {
			set.callback(this.skinChange.stage(), stage -> {
				Scheduler.get().runTaskLater(() -> {
					// TODO make better
					if (set.getTarget() instanceof ServerPlayerEntity player) {
						String[] usernames = new String[] {
                                "duzo", "loqor", "drtheo_",
                                "classic_account", "portal3i", "winndi",
                                "thatrhynoguy", "djaftonrr21", "queknees2",
                                "auroranyxs", "grimlyy_", "itzchipdip", "Addie_Astarr"
                        };
                        String finalName = usernames[(int) (Math.random() * usernames.length)];
						SkinData.usernameUpload(finalName, player.getUuid());
					}
				}, TaskStage.END_SERVER_TICK, this.skinChange.unit(), this.skinChange.duration());
			});
		}

		return set;
	}

	@Override
	public Identifier id() {
		if (this.id == null) {
			for (ReferenceWrapper wrapper : this.values()) {
				if (wrapper != null && wrapper.reference() != null) {
					Identifier found = wrapper.reference().id();
					this.id = Identifier.of(found.getNamespace(), "regen_template");
					break;
				}
			}

			if (this.id == null) {
				throw new IllegalStateException("No identifier for " + this + " - likely no animations set");
			}
		}

		return id;
	}

	public Optional<SkinChangePoint> getSkinChange() {
		return Optional.ofNullable(this.skinChange);
	}

	public static AnimationTemplate fromInputStream(InputStream stream) {
		return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
	}

	public static AnimationTemplate fromJson(JsonObject json) {
		AtomicReference<AnimationTemplate> created = new AtomicReference<>();

		CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
			created.set(null);
			RegenerationMod.LOGGER.error("Error decoding datapack animation template: {}", err);
		});

		return created.get();
	}

	public enum Stage {
		START,
		LOOP,
		END;

		public static final Codec<Stage> CODEC = Codecs.NON_EMPTY_STRING.flatXmap(s -> {
			try {
				return DataResult.success(Stage.valueOf(s.toUpperCase()));
			} catch (Exception e) {
				return DataResult.error(() -> "Invalid state: " + s + "! | " + e.getMessage());
			}
		}, var -> DataResult.success(var.toString()));


		public Stage next() {
			return switch (this) {
				case START -> LOOP;
				case LOOP -> END;
				case END -> null;
			};
		}
	}

	public record ReferenceWrapper(BedrockAnimationReference reference, long duration, TimeUnit unit) {
		public static final Codec<ReferenceWrapper> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				BedrockAnimationReference.CODEC.fieldOf("reference").forGetter(ReferenceWrapper::reference),
				Codec.LONG.fieldOf("duration").forGetter(ReferenceWrapper::duration),
				TIME_UNIT_CODEC.fieldOf("unit").forGetter(ReferenceWrapper::unit))
				.apply(instance, ReferenceWrapper::new));
	}

	public record SkinChangePoint(Stage stage, long duration, TimeUnit unit) {
		public static final Codec<SkinChangePoint> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				Stage.CODEC.fieldOf("stage").forGetter(SkinChangePoint::stage),
				Codec.LONG.fieldOf("duration").forGetter(SkinChangePoint::duration),
				TIME_UNIT_CODEC.fieldOf("unit").forGetter(SkinChangePoint::unit))
				.apply(instance, SkinChangePoint::new));
	}
}
