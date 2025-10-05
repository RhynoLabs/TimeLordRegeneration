package dev.amble.timelordregen.api;

import dev.amble.timelordregen.data.Attachments;
import net.minecraft.entity.LivingEntity;

import java.util.Optional;

public interface RegenerationCapable {
	default RegenerationInfo getRegenerationInfo() {
		if (!(this instanceof LivingEntity living)) throw new UnsupportedOperationException("This method is only default for LivingEntity instances. Override it and implement it");

		return getLivingInfo(living);
	}

	default Optional<RegenerationInfo> withInfo() {
		return Optional.ofNullable(this.getRegenerationInfo());
	}

	default void tickRegeneration() {
		RegenerationInfo info = this.getRegenerationInfo();
		if (info != null) {
			if (!(this instanceof LivingEntity living)) throw new UnsupportedOperationException("This method is only default for LivingEntity instances. Override it and implement it");

			info.tick(living);
		}
	}

	static RegenerationInfo getLivingInfo(LivingEntity entity) {
		return entity.getAttachedOrCreate(Attachments.REGENERATION);
	}
}
