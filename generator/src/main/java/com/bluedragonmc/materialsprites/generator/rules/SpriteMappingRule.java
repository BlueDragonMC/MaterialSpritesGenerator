package com.bluedragonmc.materialsprites.generator.rules;

import com.bluedragonmc.materialsprites.generator.MaterialSpritesGenerator;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SpriteMappingRule implements MappingRule {
	/** The atlas used when a rule does not specify one. */
	private static final String DEFAULT_ATLAS = "blocks";

	private final MappingRule.Abstract base;
	private @Nullable String atlas;
	private @Nullable Integer color;

	public SpriteMappingRule(MappingRule.Abstract base) {
		this.base = base;
	}

	@Contract("_ -> this")
	public SpriteMappingRule atlas(@Nullable String atlas) {
		this.atlas = atlas;
		return this;
	}

	@Contract("_ -> this")
	public SpriteMappingRule color(@Nullable Integer color) {
		this.color = color;
		return this;
	}

	@Override
	public boolean matches(String name) {
		if (!this.base.test(name))
			return false;
		List<String> atlasKeys = MaterialSpritesGenerator.ATLASES.get(this.atlas == null ? DEFAULT_ATLAS : this.atlas);
		return atlasKeys == null || atlasKeys.contains(this.base.process(name));
	}

	@Override
	public JsonObject componentData(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "sprite");
		if (this.atlas != null)
			json.addProperty("atlas", this.atlas);
		json.addProperty("key", this.base.process(name));
		if (this.color != null)
			json.addProperty("color", this.color);
		return json;
	}
}
