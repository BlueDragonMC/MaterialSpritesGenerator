package com.bluedragonmc.materialsprites.generator.rules;

import com.google.gson.JsonObject;

public final class CodeMappingRule implements MappingRule {
	private final MappingRule.Abstract base;

	public CodeMappingRule(MappingRule.Abstract base) {
		this.base = base;
	}

	@Override
	public boolean matches(String name) {
		return this.base.test(name);
	}

	@Override
	public JsonObject componentData(String name) {
		JsonObject json = new JsonObject();
		// The only code rule is the "nothing" fallback used for air.
		json.addProperty("type", "nothing");
		return json;
	}
}
