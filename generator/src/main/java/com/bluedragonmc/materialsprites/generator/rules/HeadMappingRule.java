package com.bluedragonmc.materialsprites.generator.rules;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;

public final class HeadMappingRule implements MappingRule {
	private final MappingRule.Abstract base;
	private boolean hat = true;

	public HeadMappingRule(MappingRule.Abstract base) {
		this.base = base;
	}

	@Contract(" -> this")
	public HeadMappingRule noHat() {
		this.hat = false;
		return this;
	}

	@Override
	public boolean matches(String name) {
		return this.base.test(name);
	}

	@Override
	public JsonObject componentData(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "head");
		json.addProperty("texture", this.base.process(name));
		if (!this.hat)
			json.addProperty("hat", false);
		return json;
	}
}
