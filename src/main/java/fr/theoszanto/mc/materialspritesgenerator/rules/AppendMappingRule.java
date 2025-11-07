package fr.theoszanto.mc.materialspritesgenerator.rules;

public class AppendMappingRule extends MappingRule.Delegating {
	private final String append;

	public AppendMappingRule(MappingRule.Abstract delegate, String append) {
		super(delegate);
		this.append = append;
	}

	@Override
	public String process(String name) {
		return super.process(name) + this.append;
	}
}
