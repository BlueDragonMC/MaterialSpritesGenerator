package fr.theoszanto.mc.materialspritesgenerator.rules;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.Function;

public final class MultiMappingRule implements MappingRule {
	private final List<MappingRule> rules;

	public MultiMappingRule(MappingRule.Abstract base, Function<MappingRule.Abstract, List<MappingRule>> rules) {
		this.rules = rules.apply(base);
	}

	@Override
	public boolean matches(String name) {
		return this.rules.stream().anyMatch(rule -> rule.matches(name));
	}

	@Override
	public String componentCode(String name) {
		for (MappingRule rule : this.rules)
			if (rule.matches(name))
				return rule.componentCode(name);
		throw new IllegalStateException(); // Impossible if #matches() returned true
	}

	@Override
	public Component component(String name) {
		for (MappingRule rule : this.rules)
			if (rule.matches(name))
				return rule.component(name);
		throw new IllegalStateException(); // Impossible if #matches() returned true
	}
}
