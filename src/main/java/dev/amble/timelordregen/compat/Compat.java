package dev.amble.timelordregen.compat;

import dev.amble.timelordregen.compat.ait.AITCompat;

public class Compat {
	public static void init() {
		if (DependencyChecker.HAS_AIT) {
			AITCompat.init();
		}
	}
}
