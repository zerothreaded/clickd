package com.clickd.server;


public class UtilitiesTest {

//	private void copyFileUsingJava7Files(File source, File dest) throws IOException {
//	    Files.copy(source.toPath(), dest.toPath());
//	}
//
//	@Test
//	public void renameImages() throws Exception {
//		String dataDirectoryName = "C:\\sandbox\\data\\fromprod\\images\\users";
//		
//		File dataDirectory = new File(dataDirectoryName);
//		ArrayList<String> names = new ArrayList<String>(Arrays.asList(dataDirectory.list()));
//		for (String fileName : names) {
//			String decodedFileName = URLDecoder.decode(fileName);
//			System.out.println("File Name to translate [" + decodedFileName + "]");
//			StringTokenizer tokenizer = new StringTokenizer(decodedFileName, "\\");
//			while (tokenizer.hasMoreTokens()) {
//				String token = tokenizer.nextToken();
//				if (token.endsWith(".jpg")) {
//					File currentFile = new File(dataDirectoryName, decodedFileName);
//					File newFile = new File(dataDirectoryName, token);
//					boolean created = newFile.createNewFile();
//					// FileUtils.copyFile(currentFile, newFile);
//					copyFileUsingJava7Files(currentFile, newFile);
//
//				}
//				int wait;
//			}
//		}
//	}
}
