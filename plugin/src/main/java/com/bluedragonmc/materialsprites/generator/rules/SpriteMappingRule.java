package com.bluedragonmc.materialsprites.generator.rules;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import com.bluedragonmc.materialsprites.generator.MaterialSpritesGenerator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class SpriteMappingRule implements MappingRule {
	private final MappingRule.Abstract base;
	private @Nullable String atlas;
	private @Nullable TextColor color;

	public SpriteMappingRule(MappingRule.Abstract base) {
		this.base = base;
	}

	@Contract("_ -> this")
	public SpriteMappingRule atlas(@Nullable String atlas) {
		this.atlas = atlas;
		return this;
	}

	@Contract("_ -> this")
	public SpriteMappingRule color(@Nullable TextColor color) {
		this.color = color;
		return this;
	}

	@Override
	public boolean matches(String name) {
		if (!this.base.test(name))
			return false;
		List<String> atlasKeys = MaterialSpritesGenerator.ATLASES.get(Objects.requireNonNullElse(this.atlas, SpriteObjectContents.DEFAULT_ATLAS.value()));
		return atlasKeys == null || atlasKeys.contains(this.base.process(name));
	}

	@Override
	public String componentCode(String name) {
		StringBuilder code = new StringBuilder("sprite(");

		if (this.atlas != null)
			code.append('"').append(this.atlas).append("\", ");

		code.append('"').append(this.base.process(name)).append('"');

		if (this.color != null)
			code.append(", ").append(this.color.value());

		return code.append(')').toString();
	}

	@Override
	public JsonObject componentData(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "sprite");
		if (this.atlas != null)
			json.addProperty("atlas", this.atlas);
		json.addProperty("key", this.base.process(name));
		if (this.color != null)
			json.addProperty("color", this.color.value());
		return json;
	}

	@SuppressWarnings("PatternValidation") // Key#key
	@Override
	public Component component(String name) {
		return Component.object(ObjectContents.sprite(this.atlas == null ? SpriteObjectContents.DEFAULT_ATLAS : Key.key(this.atlas), Key.key(this.base.process(name))))
				.color(Objects.requireNonNullElse(this.color, NamedTextColor.WHITE));
	}
}
