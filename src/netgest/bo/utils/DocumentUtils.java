package netgest.bo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import netgest.bo.def.boDefAttribute;
import netgest.bo.message.server.mail.MailUtil;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.io.iFile;

public class DocumentUtils {
	private static Logger logger = Logger.getLogger(DocumentUtils.class.getName());

	public static File createZipFile(String zipFilename, ArrayList<File> files) throws boRuntimeException2 {
		File result = null;

		if (zipFilename != null && !zipFilename.isEmpty() && files != null && !files.isEmpty()) {
			FileOutputStream fos = null;
			ZipOutputStream zos = null;

			try {
				ArrayList<String> filenames = new ArrayList<String>();
				String suffix = MailUtil.getExtension(zipFilename);
				String prefix = MailUtil.getFirstPart(zipFilename);

				File tempFile = MailUtil.createTempFile(prefix, suffix);
				fos = new FileOutputStream(tempFile);
				zos = new ZipOutputStream(fos);

				for (File file : files) {
					if (file != null) {
						FileInputStream fis = null;
						String filename = createNewFilename(filenames, file.getName());
						filenames.add(filename);

						try {
							fis = new FileInputStream(file);
							ZipEntry zipEntry = new ZipEntry(filename);
							zos.putNextEntry(zipEntry);

							byte[] bytes = new byte[1024];
							int length;
							while ((length = fis.read(bytes)) >= 0) {
								zos.write(bytes, 0, length);
							}

							zos.closeEntry();
						} catch (Exception e) {
							throw new boRuntimeException2("File: " + filename + "; Error: " + e.getMessage(), e);
						} finally {
							try {
								if (fis != null) {
									fis.close();
								}
							} catch (Exception e) {
								// ignore
							}
						}
					}
				}

				result = tempFile;
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			} finally {
				try {
					if (zos != null) {
						zos.close();
					}
				} catch (Exception e) {
					// ignore
				}

				try {
					if (fos != null) {
						fos.close();
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return result;
	}

	private static String createNewFilename(ArrayList<String> filenames, String filename) {
		String result = filename;

		if (filenames != null && !filenames.isEmpty() && filename != null && !filename.isEmpty()) {
			int count = 1;

			while (filenames.contains(result)) {
				result = MailUtil.getFirstPart(filename) + count++ + MailUtil.getExtension(filename);
			}
		}

		return result;
	}

	private static ArrayList<File> getTempFilesFromDocuments(ArrayList<boObject> documents) throws boRuntimeException2 {
		ArrayList<File> result = new ArrayList<File>();

		if (documents != null && !documents.isEmpty()) {
			try {
				for (boObject document : documents) {
					result.addAll(getTempFilesFromDocument(document));
				}
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			}
		}

		return result;
	}

	private static ArrayList<File> getTempFilesFromDocuments(boObjectList documents) throws boRuntimeException2 {
		ArrayList<File> result = new ArrayList<File>();

		if (documents != null) {
			try {
				documents.beforeFirst();

				while (documents.next()) {
					result.addAll(getTempFilesFromDocument(documents.getObject()));
				}
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			}
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private static ArrayList<File> getTempFilesFromDocument(boObject document) throws boRuntimeException2 {
		ArrayList<File> result = new ArrayList<File>();

		if (document != null) {
			try {
				boAttributesArray attributes = document.getAttributes();
				Enumeration attributesEnum = attributes.elements();

				while (attributesEnum.hasMoreElements()) {
					AttributeHandler attribute = (AttributeHandler) attributesEnum.nextElement();

					if (boDefAttribute.ATTRIBUTE_BINARYDATA.equals(attribute.getDefAttribute().getAtributeDeclaredType())) {
						iFile file = attribute.getValueiFile();

						if (file != null) {
							File tempFile = MailUtil.iFileToTempFile(file);

							if (tempFile != null) {
								result.add(tempFile);
							} else {
								// throw new boRuntimeException2("File: " + file.getName() + "; Error: Error creating the temp file.");
							}
						}
					}
				}
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			}
		}

		return result;
	}

	public static File getZipFileFromDocuments(String zipFilename, ArrayList<boObject> documents) throws boRuntimeException2 {
		File result = null;

		if (zipFilename != null && !zipFilename.isEmpty() && documents != null && !documents.isEmpty()) {
			try {
				ArrayList<File> files = getTempFilesFromDocuments(documents);

				if (files != null) {
					result = createZipFile(zipFilename, files);
				}
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			}
		}

		return result;
	}

	public static File getZipFileFromDocuments(ArrayList<boObject> documents) throws boRuntimeException2 {
		return getZipFileFromDocuments("documents.zip", documents);
	}

	public static File getZipFileFromDocuments(String zipFilename, boObjectList documents) throws boRuntimeException2 {
		File result = null;

		if (zipFilename != null && !zipFilename.isEmpty() && documents != null && !documents.isEmpty()) {
			try {
				ArrayList<File> files = getTempFilesFromDocuments(documents);

				if (files != null) {
					result = createZipFile(zipFilename, files);
				}
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			}
		}

		return result;
	}

	public static File getZipFileFromDocuments(boObjectList documents) throws boRuntimeException2 {
		return getZipFileFromDocuments("documents.zip", documents);
	}

	public static File getZipFileFromDocuments(String zipFilename, bridgeHandler documents) throws boRuntimeException2 {
		File result = null;

		if (zipFilename != null && !zipFilename.isEmpty() && documents != null && !documents.isEmpty()) {
			try {
				ArrayList<File> files = getTempFilesFromDocuments(documents);

				if (files != null) {
					result = createZipFile(zipFilename, files);
				}
			} catch (Exception e) {
				logger.severe(e);
				throw new boRuntimeException2(e);
			}
		}

		return result;
	}

	public static File getZipFileFromDocuments(bridgeHandler documents) throws boRuntimeException2 {
		return getZipFileFromDocuments("documents.zip", documents);
	}
}