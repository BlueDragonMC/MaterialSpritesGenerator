package com.bluedragonmc.materialsprites.generator.rules;

public class PrependMappingRule extends MappingRule.Delegating {
	private final String prepend;

	public PrependMappingRule(MappingRule.Abstract delegate, String prepend) {
		super(delegate);
		this.prepend = prepend;
	}

	@Override
	public String process(String name) {
		return this.prepend + super.process(name);
	}
}
