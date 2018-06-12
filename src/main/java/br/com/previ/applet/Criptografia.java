package src.main.java.br.com.previ.applet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Criptografia {

	public Criptografia() {

	}

	public static void encryptFile(String srcPath, String destPath, SecretKey key, IvParameterSpec iv, String algorithm,
			final Optional<Provider> provider) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = null;

		if (provider.isPresent()) {
			cipher = Cipher.getInstance(algorithm, provider.get());
		} else {
			cipher = Cipher.getInstance(algorithm);
		}

		File sourceFile = new File(srcPath);
		File encryptedFile = new File(destPath);
		InputStream inStream = null;
		OutputStream outStream = null;
		// Inicializa a cifra para o processo de encriptacao
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);

		// Inicializa o input e o output streams
		inStream = new FileInputStream(sourceFile);
		outStream = new FileOutputStream(encryptedFile);

		byte[] buffer = new byte[256];
		int len;

		while ((len = inStream.read(buffer)) > 0) {
			// Para criptografar/descriptografar varios blocos usa-se o metodo update().
			outStream.write(cipher.update(buffer, 0, len));
			outStream.flush();
		}

		// metodo doFinal() para escrever.
		outStream.write(cipher.doFinal());
		inStream.close();
		outStream.close();

	}

	public static void decryptFile(String srcPath, String destPath, SecretKey key, IvParameterSpec iv, String algorithm)
			throws InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, FileNotFoundException,
			IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		Cipher cipher = Cipher.getInstance(algorithm);

		File encryptedFile = new File(srcPath);
		File decryptedFile = new File(destPath);
		InputStream inStream = null;
		OutputStream outStream = null;

		// Inicializa o cipher para descriptografar
		cipher.init(Cipher.DECRYPT_MODE, key, iv);

		// Inicializa o input e o output streams
		inStream = new FileInputStream(encryptedFile);
		outStream = new FileOutputStream(decryptedFile);

		byte[] buffer = new byte[256];
		int len;

		while ((len = inStream.read(buffer)) > 0) {
			outStream.write(cipher.update(buffer, 0, len));
			outStream.flush();
		}

		outStream.write(cipher.doFinal());
		inStream.close();
		outStream.close();

	}

	public static byte[] encryptKey(byte[] inputBytes, PublicKey publicKey, String algorithm) throws UnsupportedEncodingException,
			InvalidKeyException, InvalidAlgorithmParameterException, FileNotFoundException, IOException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		Cipher cipher = Cipher.getInstance(algorithm);

		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(inputBytes);
	}

	public static byte[] decryptKey(byte[] inputBytes, PrivateKey key, String algorithm) throws UnsupportedEncodingException,
			InvalidKeyException, InvalidAlgorithmParameterException, FileNotFoundException, IOException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		Cipher cipher = Cipher.getInstance(algorithm);

		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inputBytes);
	}

	public static PublicKey getPublicKey(String pathChavePublica) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, InvalidKeySpecException, CertificateException {

		// Busca a chave pública do certificado do caminho informado.
		File file = new File(pathChavePublica);
		
	    CertificateFactory fact = CertificateFactory.getInstance("X.509");
	    FileInputStream is = new FileInputStream (file);
	    X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
	    PublicKey publicKey = cer.getPublicKey();

	    return publicKey;
	}

	public static SecretKey createKey(final String algorithm, final int keysize, final Optional<Provider> provider,
			final Optional<SecureRandom> rng) throws NoSuchAlgorithmException {
		final KeyGenerator keyGenerator;
		if (provider.isPresent()) {
			keyGenerator = KeyGenerator.getInstance(algorithm, provider.get());
		} else {
			keyGenerator = KeyGenerator.getInstance(algorithm);
		}

		if (rng.isPresent()) {
			keyGenerator.init(keysize, rng.get());
		} else {
			// not really needed for the Sun provider which handles null OK
			keyGenerator.init(keysize);
		}

		return keyGenerator.generateKey();
	}

	public static IvParameterSpec createIV(final int ivSizeBytes, final Optional<SecureRandom> rng) {
		final byte[] iv = new byte[ivSizeBytes];
		final SecureRandom theRNG = rng.orElse(new SecureRandom());
		theRNG.nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	public static IvParameterSpec readIV(final int ivSizeBytes, final InputStream is) throws IOException {
		final byte[] iv = new byte[ivSizeBytes];
		int offset = 0;
		while (offset < ivSizeBytes) {
			final int read = is.read(iv, offset, ivSizeBytes - offset);
			if (read == -1) {
				throw new IOException("Too few bytes for IV in input stream");
			}
			offset += read;
		}
		return new IvParameterSpec(iv);
	}

}
