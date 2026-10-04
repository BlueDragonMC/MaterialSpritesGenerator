package com.bluedragonmc.materialsprites.generator.rules;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class CodeMappingRule implements MappingRule {
	private final MappingRule.Abstract base;
	private @Nullable Component resolved;

	public CodeMappingRule(MappingRule.Abstract base) {
		this.base = base;
	}

	@Contract("_ -> this")
	public CodeMappingRule resolved(@Nullable Component resolved) {
		this.resolved = resolved;
		return this;
	}

	@Override
	public boolean matches(String name) {
		return this.base.test(name);
	}

	@Override
	public String componentCode(String name) {
		return this.base.process(name);
	}

	@Override
	public JsonObject componentData(String name) {
		JsonObject json = new JsonObject();
		// The only code rule is the "nothing" fallback used for air.
		json.addProperty("type", "nothing");
		return json;
	}

	@Override
	public Component component(String name) {
		return Objects.requireNonNullElse(this.resolved, Component.empty());
	}
}
