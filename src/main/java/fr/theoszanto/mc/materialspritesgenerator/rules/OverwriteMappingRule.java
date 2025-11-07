package fr.theoszanto.mc.materialspritesgenerator.rules;

public class OverwriteMappingRule extends MappingRule.Delegating {
	private final String overwrite;

	public OverwriteMappingRule(MappingRule.Abstract delegate, String overwrite) {
		super(delegate);
		this.overwrite = overwrite;
	}

	@Override
	public String process(String name) {
		return this.overwrite;
	}
}
