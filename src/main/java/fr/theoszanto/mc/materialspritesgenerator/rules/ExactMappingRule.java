package fr.theoszanto.mc.materialspritesgenerator.rules;

public class ExactMappingRule extends MappingRule.Delegating {
	private final String exact;

	public ExactMappingRule(MappingRule.Abstract delegate, String exact) {
		super(delegate);
		this.exact = exact;
	}

	@Override
	public boolean test(String name) {
		return super.test(name) && name.equals(this.exact);
	}
}
