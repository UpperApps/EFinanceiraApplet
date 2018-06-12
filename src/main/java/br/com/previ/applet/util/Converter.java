package src.main.java.br.com.previ.applet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;


/**
 * Classe utilitária para realização de conversões.
 * @author Rodrigo Melo
 * 
 */
public final class Converter {

	public Converter() {
			
	}

	/**
	 * 
	 * @param file Arquivo que será convertido para bytes e depois para String em Base64.
	 * @return String em Base64.
	 */
	public static String convertToBase64(File file) {

		byte[] byteArray = generateByteArray(file);
		String byteArrayBase64 = Base64.getEncoder().encodeToString(byteArray);

		return byteArrayBase64;
	}
	/**
	 * 
	 * @param text Texto a ser convertido para Base64.
	 * @param charSet Charset a ser utilizado. Ex.: UTF-8.
	 * @return String em Base64.
	 */
	public static String convertToBase64(String text, String charSet) {

		String byteArrayBase64 = Base64.getEncoder().encodeToString(text.getBytes(Charset.forName(charSet)));
		return byteArrayBase64;
	}

	/**
	 * 
	 * @param arquivo Converte um arquivo em um array de bytes.
	 * @return byte[]
	 */
	public static byte[] generateByteArray(File arquivo) {

		int len = (int) arquivo.length();
		byte[] bytes = new byte[len];
		FileInputStream inFile = null;
		try {
			inFile = new FileInputStream(arquivo);
			inFile.read(bytes, 0, len);
			inFile.close();
		} catch (FileNotFoundException fnfex) {
			fnfex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}

		return bytes;
	}

}
