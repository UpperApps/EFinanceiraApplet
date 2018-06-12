package src.main.java.br.com.previ.applet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Classe reposável pela compactação de arquivos.
 * @author prvb87
 *
 */
public class CompactaArquivo {
	
	/**
	 * 
	 * @param nomeArquivo
	 * @param diretorio
	 * @return String Retorna o caminho e o nome do arquivo compactado.
	 * 
	 */
	public static String compactarArquivoGzip(String nomeArquivo, String diretorio) {

		if ((nomeArquivo == null) || (nomeArquivo.length() == 0)) {
			throw new IllegalArgumentException("Não é possível compactar arquivo vazio");
		}

		String arquivoPathOriginal = diretorio + nomeArquivo;

		String arquivoPathGzip = diretorio + nomeArquivo + ".gz";

		try {
			FileInputStream fis = new FileInputStream(arquivoPathOriginal);
			FileOutputStream fos = new FileOutputStream(arquivoPathGzip);
			GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				gzipOS.write(buffer, 0, len);
			}
			// fechando todos os recursos usados
			gzipOS.close();
			fos.close();
			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return arquivoPathGzip;

	}

}
