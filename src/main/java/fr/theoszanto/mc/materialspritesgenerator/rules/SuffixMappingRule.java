package fr.theoszanto.mc.materialspritesgenerator.rules;

public class SuffixMappingRule extends MappingRule.Delegating {
	private final String suffix;
	private final boolean strip;

	public SuffixMappingRule(MappingRule.Abstract delegate, String suffix, boolean strip) {
		super(delegate);
		this.suffix = suffix;
		this.strip = strip;
	}

	@Override
	public boolean test(String name) {
		return super.test(name) && name.endsWith(this.suffix);
	}

	@Override
	public String process(String name) {
		String processed = super.process(name);
		return this.strip ? processed.substring(0, processed.length() - this.suffix.length()) : processed;
	}
}
