package netgest.utils;

import java.io.File;

import netgest.io.BasiciFile;
import netgest.io.DBiFile;

public class FileUtils {
	public static boolean isFilenameValid(String filename) {
		boolean result = false;

		if (filename != null && !filename.isEmpty()) {
			try {
				File f = TempFile.createTempFile(filename);
				f.getCanonicalPath();
				result = true;
			} catch (Exception e) {
			}
		}

		return result;
	}

	public static boolean isPathAnIfile(String path) {
		boolean result = false;

		if (path != null && !path.isEmpty() && (path.startsWith("//" + DBiFile.IFILE_SERVICE_NAME) || path.startsWith("//" + BasiciFile.IFILE_SERVICE_NAME))) {
			result = true;
		}

		return result;
	}
}