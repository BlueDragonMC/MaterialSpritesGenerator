package com.bluedragonmc.materialsprites.generator.rules;

public class PrefixMappingRule extends MappingRule.Delegating {
	private final String prefix;
	private final boolean strip;

	public PrefixMappingRule(MappingRule.Abstract delegate, String prefix, boolean strip) {
		super(delegate);
		this.prefix = prefix;
		this.strip = strip;
	}

	@Override
	public boolean test(String name) {
		return super.test(name) && name.startsWith(this.prefix);
	}

	@Override
	public String process(String name) {
		String processed = super.process(name);
		return this.strip ? processed.substring(this.prefix.length()) : processed;
	}
}
