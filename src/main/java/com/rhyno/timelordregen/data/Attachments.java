package com.rhyno.timelordregen.data;

import com.rhyno.timelordregen.RegenerationMod;
import com.rhyno.timelordregen.api.RegenerationInfo;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class Attachments {
	public static final AttachmentType<RegenerationInfo> REGENERATION = AttachmentRegistry.<RegenerationInfo>builder().persistent(RegenerationInfo.CODEC).initializer(RegenerationInfo::new).copyOnDeath().buildAndRegister(RegenerationMod.id("regeneration"));

	public static void init() {

	}
}
