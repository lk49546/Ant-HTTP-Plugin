package hr.fina.ci.ant.http.task;

import java.util.Collection;
import java.util.HashSet;

import org.apache.tools.ant.types.DataType;

/**
 * Element that contains collection of params
 * 
 * @author lkelava
 *
 */
public class ParamsNode extends DataType implements Validable {
	
	private Collection<ParamNode> params;

	public ParamsNode() {
		super();
		params = new HashSet<>();
	}
	
	public void addConfiguredParam(ParamNode param) {
		params.add(param);
	}

	public Collection<ParamNode> getParams() {
		return params;
	}
	
	/* (non-Javadoc)
	 * @see hr.fina.ci.ant.http.task.Validable#isValid()
	 */
	public boolean isValid() {
		return !params.isEmpty();
	}
	

}
