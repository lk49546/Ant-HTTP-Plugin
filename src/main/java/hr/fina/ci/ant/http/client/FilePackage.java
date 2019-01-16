package hr.fina.ci.ant.http.client;

import java.io.File;

/**
 * Class representing multipart file we are posting.
 * It contains name and value (File).
 * 
 * @author lkelava
 *
 */
public class FilePackage {
	private final String key;
	private final File file;
	public FilePackage(String key, File file) {
		super();
		this.key = key;
		this.file = file;
	}
	public String getKey() {
		return key;
	}
	public File getFile() {
		return file;
	}
	
	
}
