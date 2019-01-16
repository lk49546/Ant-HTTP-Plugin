package hr.fina.ci.ant.http.task;

import java.io.File;

import org.apache.tools.ant.types.DataType;

/**
 * Key-value pair for file node
 * 
 * @author lkelava
 *
 */
public class FileNode extends DataType implements Validable {
	
	private String name;
	private File file;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	/* (non-Javadoc)
	 * @see hr.fina.ci.ant.http.task.Validable#isValid()
	 */
	@Override
	public boolean isValid() {
		return name != null && file != null && !name.isEmpty();
	}
	

}
