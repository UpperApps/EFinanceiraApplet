package src.main.java.br.com.previ.applet;

import java.applet.Applet;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JOptionPane;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import netscape.javascript.JSObject;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import src.main.java.br.com.previ.applet.efinanceira.lotecriptografado.EFinanceira;
import src.main.java.br.com.previ.applet.efinanceira.lotecriptografado.EFinanceira.LoteCriptografado;
import src.main.java.br.com.previ.applet.efinanceira.lotecriptografado.ObjectFactoryCriptografia;
import src.main.java.br.com.previ.applet.util.ConstrutorXML;
import src.main.java.br.com.previ.applet.util.Converter;
import br.gov.frameworkdemoiselle.certificate.applet.action.AbstractAppletExecute;

import com.sun.java.browser.dom.DOMAccessException;
import com.sun.java.browser.dom.DOMAccessor;
import com.sun.java.browser.dom.DOMAction;
import com.sun.java.browser.dom.DOMService;
import com.sun.java.browser.dom.DOMUnsupportedException;

public class EFinanceiraApplet extends AbstractAppletExecute {
	
	// Parâmetros de definição da URL de conexão com o serviço da e-Financeira.
	private static final String URL_PRODUCAO = "https://efinanc.receita.fazenda.gov.br/WsEFinanceiraCripto/WsRecepcaoCripto.asmx";
	private final String URL_PRE_PRODUCAO = "https://preprod-efinanc.receita.fazenda.gov.br/WsEFinanceiraCripto/WsRecepcaoCripto.asmx";

	// Parâmetros de configuração dos thumbprints da receita federal que serão usados
	// no envio do lote de eventos criptografado.
	private final String IDCERTPREPROD = "88edffa74bf7984197c1749ba96f56372dc02bac";
	private final String IDCERTPROD = "4f96a2a59ef1248411e0ec4b3aed7f3c3e2d6727";

	// Local da chave pública no sistema de arquivos.
	public static final String PATH_CHAVE_PUBLICA_PREPROD = "X:\\E-Financeira\\Chave_Publica\\preprod-efinancentreposto.receita.fazenda.gov.br.cer";
	public static final String PATH_CHAVE_PUBLICA_PROD = "X:\\E-Financeira\\Chave_Publica\\efinanc_web.cer";

	// Define o algorítmo de criptografia como AES.
	private static final String AES_ALGORITHM = "AES/CBC/PKCS7Padding";
	private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
	private static final int KEY_SIZE_AES = 128;
	private static final int IV_SIZE = 16;

	// Diretório onde ficam os arquivos de eventos do e-Financeira.
	private final String DIRETORIO = "X:\\E-Financeira\\";

	// Parâmetros de configuração do proxy.
	private final int PORTA_PROXY = 7777;
	private final String ENDERECO_PROXY = "myproxy.com.br";

	// NameSpace para geração de UUIDs, conforme
	// https://tools.ietf.org/html/rfc4122#page-30
	private final String NameSpace_DNS = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
	
	private String tipoEvento = null;

	
	public void execute(KeyStore keystore, String alias, Applet applet) {

		/**
		 * Rotina principal
		 */

		/**
		 * 1.Assina os arquivos de lote de eventos que serão enviados para a RF.
		 **/

		// Busca todos os arquivos XML do diretório
		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		};
		File dir = new File(DIRETORIO);
		File[] files = dir.listFiles(filter);

		// Para cada artquivo encontrado no diretório, realiza a assinatura dos eventos, a criptografia e a compactação
		// e depois envia o arquivo para a RF via Webservice SAOP.
		for (int intera = 0; intera < files.length; intera++) {
			
			String xmlRetorno = null;

			try {

				File arquivoOrigem = new File(DIRETORIO + files[intera].getName());
				
				String arquivoOrigemEmString = converterArquivoEmString(arquivoOrigem);
				
				// Remove quebras de linha e espaços indesejados, como TABs por exemplo.
				String arquivoXml = arquivoOrigemEmString;
				arquivoXml = removeCaracteresInvalidos(arquivoXml);

				// ArrayList que receberá os eventos financeiros depois de assinados. 
				ArrayList<String> loopAssinados = new ArrayList<String>();

				Matcher matcher = Pattern.compile("<evento.*?>(.*?)<\\/evento>").matcher(arquivoXml);

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);

				while (matcher.find()) {

					// Transforma o evento financeiro encontrado no texto em um documento XML.
					Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(matcher.group(1).trim().getBytes("UTF-8")));
					
					this.tipoEvento = obtemElementoAssinar(doc);
					
					// Assina o evento financeiro.
					assinarArquivo(keystore, alias, applet, doc, this.tipoEvento);

					// Transforma o documento XML assinado em String e o adiciona ao ArrayList. 
					StringWriter writer = new StringWriter();
					TransformerFactory tf = TransformerFactory.newInstance();
					Transformer trans = tf.newTransformer();
					trans.transform(new DOMSource(doc), new StreamResult(writer));

					loopAssinados.add(writer.getBuffer().toString());
					// -------------------------------------------------------------------------------------------------------//
				}

				// Formata o documento XML com os evento assinados incluíndo novament a tag evento. 
				String xmlAssindoFormatado = formataXml(loopAssinados);

				// Remove os cabeçalhos indesejados gerados durante o processo de assinatura dos eventos.
				xmlAssindoFormatado = removeHeaderXML(xmlAssindoFormatado);
				// Inclui o cabeçalho correto no arquivo xml.
				xmlAssindoFormatado = incluiHeaderXML(xmlAssindoFormatado);
				// Limpa caracteres indesejados do arquivo assinado.
				xmlAssindoFormatado = removeCaracteresInvalidos(xmlAssindoFormatado);

				// Cria o arquivo assinado em disco.
				File arquivoAssinado = new File(DIRETORIO + arquivoOrigem.getName().replace(".xml", "-Assinado.xml"));

				// Escreve o conteúdo do arquivo assinado.
				FileWriter arquivoAssinadoWriter = new FileWriter(arquivoAssinado);
				arquivoAssinadoWriter.write(xmlAssindoFormatado);
				arquivoAssinadoWriter.close();

				/** 2.Definir a chave de criptografia. **/

				// Define o provider (BounceCastle) e o SecureRandom.
				SecureRandom secureRandom = new SecureRandom();
				Provider bcProvider = new BouncyCastleProvider();

				// Gera a chave de criptografia.
				SecretKey chaveAES = Criptografia.createKey("AES", KEY_SIZE_AES, Optional.of(bcProvider), Optional.of(secureRandom));
				// Gera o vetor de inicialização (IV).
				IvParameterSpec iv = Criptografia.createIV(IV_SIZE, Optional.of(secureRandom));

				/**
				 * 3.Compactar o arquivo xml assindo do lote a ser enviado com gzip.
				 **/

				// Compacta o arquivo e obtém caminho e nome compactados.
				String arquivoPathGzip = CompactaArquivo.compactarArquivoGzip(arquivoAssinado.getName(), DIRETORIO);
				File arquivoGZIP = new File(arquivoPathGzip);

				/**
				 * 3.Criptografar o arquivo compactado a ser enviado (utilizando a Chave de Criptografia, definida no item 1).
				 **/

				// Define o nome do arquivo criptografado.
				String arquivoPathCriptografado = DIRETORIO + arquivoGZIP.getName() + "ENC.gz";
				Criptografia.encryptFile(arquivoPathGzip, arquivoPathCriptografado, chaveAES, iv, AES_ALGORITHM,
						Optional.of(bcProvider));

				/**
				 * 4.Criptografar a Chave de Criptografia, definida no item 1, com a Chave Pública do Certificado Digital ICP Brasil do
				 * Servidor e-Financeira.
				 **/
				
				// Busca o nome do Banco de Dados que irá definir o ambiente onde será executada a aplicação.
				// O nome do Banco é buscado na página HTML onde roda o applet.
				// Optional<String> ambiente = Optional.of(retornaBanco(applet));
				
				// Configura o ambiente em que está sendo executada a aplicação.
				String ambiente = retornaBanco(applet);

				// Define o certificado (thumbprint)o certificado chave pública do servidor da e-Financeira a ser utilizado
				// conforme o ambiente de execução.
				String idCertificado = null;
				String pathChavePublica = null;
				if (ambiente.equals("PROD")) {
					idCertificado = IDCERTPROD;
					pathChavePublica = PATH_CHAVE_PUBLICA_PROD;
				} else {
					idCertificado = IDCERTPREPROD;
					pathChavePublica = PATH_CHAVE_PUBLICA_PREPROD;
				}

				// Busca a chave privada do token A3. Esta chave pode ser usada para descriptografar o arquivo encriptado no passo 3.
				// PrivateKey chavePrivada = (PrivateKey) keystore.getKey(alias, null);

				PublicKey publicKey = Criptografia.getPublicKey(pathChavePublica);

				// Concatena a chaveAES e o IV, conforme item 2.5 do Manual para Compactação e Criptografia de dados V1.
				byte[] chaveEIvConcatenados = concatenaChaveComIV(chaveAES, iv);

				// Criptografa a chave de criptografia AES com o certificado público com algorítimo RSA de 2048 bits.
				final byte[] chaveEncriptada = Criptografia.encryptKey(chaveEIvConcatenados, publicKey, RSA_ALGORITHM);
				String chaveEncriptadaBase64 = Base64.getEncoder().encodeToString(chaveEncriptada);

				/**
				 * 5.Gerar um novo arquivo XML do e-Financeira,contendo: • Identificação de Evento de envio de Dados e-Financeira em
				 * modo Criptografado. • Chave de Criptografia • ID do certificado (thumbprint) • Mensagem criptografada
				 **/

				// Gera o id do evento de envio.
				UUID uuid = geraIDEvento(arquivoAssinado);

				// Recupera o arquivo criptografado, transforma em uma array de bytes e codifica com Base64.
				File arquivoGZIPCriptografado = new File(arquivoPathCriptografado);
				String arquivoBase64 = Converter.convertToBase64(arquivoGZIPCriptografado);

				// Inclui o envelope SOAP do arquivo XML final.
				ObjectFactoryCriptografia objectFactory = new ObjectFactoryCriptografia();

				LoteCriptografado loteCriptografado = objectFactory.createEFinanceiraLoteCriptografado();

				loteCriptografado.setId(uuid.toString());
				loteCriptografado.setChave(chaveEncriptadaBase64);
				loteCriptografado.setIdCertificado(idCertificado);
				loteCriptografado.setLote(arquivoBase64);

				EFinanceira eFinanceira = objectFactory.createEFinanceira();

				eFinanceira.setLoteCriptografado(loteCriptografado);
				
				/** 
				 * 6.Estabelecer túnel SSL com o Web Service do e-Financeira.
				 **/
				
				// Implementado no método enviaXmlCriptoZipado.
				
				/**
				 * 7.Enviar ao servidor e-Financeira, o arquivo XML (gerado no item 5).
				 **/

				xmlRetorno = enviaXmlCriptoZipado(eFinanceira, ambiente, applet, keystore, alias);

				/**
				 * 8.Receber o arquivo de retorno e passar a informação de volta para a aplicação web.
				 **/
				
				// Grava o arquivo de retorno no diretório da E-Financeira.
				geraArquivoRetornoEFinanceira(arquivoOrigem, xmlRetorno, uuid.toString());

				// Passo o retorno do serviço da e-Financeira para o IRF-WEB.
				retornaXmls(arquivoOrigemEmString, xmlRetorno, applet, intera + 1, files.length);

				// Apaga os arquivos auxiliares.
				arquivoOrigem.delete();
				arquivoAssinado.delete();
				arquivoGZIP.delete();
				arquivoGZIPCriptografado.delete();

				/**
				 * Fim da rotina principal
				 */

				// TODO Melhorar o tratamento de erros.
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro5", JOptionPane.ERROR_MESSAGE);
			} catch (SAXException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro6", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro7", JOptionPane.ERROR_MESSAGE);
			} catch (ParserConfigurationException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro8", JOptionPane.ERROR_MESSAGE);
			} catch (TransformerConfigurationException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro11", JOptionPane.ERROR_MESSAGE);
			} catch (TransformerException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro12", JOptionPane.ERROR_MESSAGE);
			} catch (NoSuchAlgorithmException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro13", JOptionPane.ERROR_MESSAGE);
			} catch (NoSuchPaddingException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro15", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidKeyException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro16", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidAlgorithmParameterException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro17", JOptionPane.ERROR_MESSAGE);
			} catch (IllegalBlockSizeException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro18", JOptionPane.ERROR_MESSAGE);
			} catch (BadPaddingException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro19", JOptionPane.ERROR_MESSAGE);
			} catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro20", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidKeySpecException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro21", JOptionPane.ERROR_MESSAGE);
			} catch (CertificateException e) {
				JOptionPane.showMessageDialog(applet, e, "Erro22", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * @param arquivoOrigem
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String converterArquivoEmString(File arquivoOrigem) throws UnsupportedEncodingException, FileNotFoundException,
			IOException {
		BufferedReader in = null;

		// Cria um BufferReader para leitura do arquivo.
		in = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "UTF-8"));

		StringBuffer arquivoOrigemEmString = new StringBuffer();
		String str = "";

		// Carrega um StringBuffer com o texto do arquivo.
		while ((str = in.readLine()) != null) {
			arquivoOrigemEmString.append(str);
		}

		in.close();
		
		return arquivoOrigemEmString.toString();
	}

	/**
	 * Remove quebras de linha e espaços indesejados, como TABs por exemplo.
	 * @param texto em formato String.
	 * @return String
	 */
	private String removeCaracteresInvalidos(String texto) {
		return texto
				.replaceAll("\n", "")
				.replaceAll("\r", "")
				.replaceAll("\r\n", "")
				.replaceAll("\b", "")
				.replaceAll("\t", "")
				.replaceAll(" +", " ")
				.replaceAll(" <", "<")
				.replaceAll("º|ª", "")
				.replaceAll("Ã", "A")
				.replaceAll("ã", "a")
				.replaceAll("Á", "A")
				.replaceAll("á", "a")
				.replaceAll("À", "A")
				.replaceAll("à", "a")
				.replaceAll("Ê", "E")
				.replaceAll("ê", "e")
				.replaceAll("É", "E")
				.replaceAll("é", "e")
				.replaceAll("Í", "I")
				.replaceAll("í", "i")
				.replaceAll("Õ", "O")
				.replaceAll("õ", "o")
				.replaceAll("Ô", "O")
				.replaceAll("ô", "o")
				.replaceAll("Ó", "O")
				.replaceAll("ó", "o")
				.replaceAll("Ú", "U")
				.replaceAll("ú", "u")
				.replaceAll("Ç", "C")
				.replaceAll("ç", "c")
				.trim();
	}

	private void geraArquivoRetornoEFinanceira(File arquivoOrigem, String xmlRetorno, String idLote) throws IOException {
		File arquivoRetorno = new File(DIRETORIO + "\\Retorno_Envio\\" + arquivoOrigem.getName().replace(".xml", "-Retorno-" + idLote + ".xml"));

		// Escreve o conteúdo do arquivo de retorno.
		FileWriter arquivoAssinadoWriter = new FileWriter(arquivoRetorno);
		arquivoAssinadoWriter.write(xmlRetorno);
		arquivoAssinadoWriter.close();
	}

	/**
	 * @param chaveAES
	 * @param iv
	 * @return
	 */
	private byte[] concatenaChaveComIV(SecretKey chaveAES, IvParameterSpec iv) {
		// String chaveIvConcatenados = chaveAES.toString() + iv.toString();
		byte[] chaveIvConcatenados = new byte[chaveAES.getEncoded().length + iv.getIV().length];
		System.arraycopy(chaveAES.getEncoded(), 0, chaveIvConcatenados, 0, chaveAES.getEncoded().length);
		System.arraycopy(iv.getIV(), 0, chaveIvConcatenados, chaveAES.getEncoded().length, iv.getIV().length);
		return chaveIvConcatenados;
	}

	private UUID geraIDEvento(File arquivoAssinado) throws SAXException, IOException, ParserConfigurationException,
			UnsupportedEncodingException {
		
		// Remove caracteres inválidos.
		String texto = converterArquivoEmString(arquivoAssinado);
		texto.replaceAll("[^0-9a-zA-Z]+", "");
		
		// Cria um DocumentBuilderFactory para transformação de arquivos xml em um document.
		DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();
		documentBuilder.setNamespaceAware(true);
		Document documentoOriginal = documentBuilder.newDocumentBuilder().parse(new InputSource(new StringReader(texto)));

		// Recupera o identificador do primeiro evento do arquivo para utilizá-lo para a geração do UUID
		// (Identificador Único Universal) que será usado no xml final que será enviado para a Receita Federal.
		String name = this.retornaIDLote(this.tipoEvento, documentoOriginal);

		// Gera o UUID
		String source = NameSpace_DNS + name;
		byte[] bytes = source.getBytes("UTF-8");
		UUID uuid = UUID.nameUUIDFromBytes(bytes);
		return uuid;
	}

	private String formataXml(ArrayList<String> loopAssinados) {

		String retorno = new String();

		for (int intera = 0; intera < loopAssinados.size(); intera++) {
			retorno = retorno + "<evento id=\"ID" + intera + "\">" + loopAssinados.get(intera) + "</evento>";
		}

		return retorno;
	}

	public int quantosElementosExistem(String node, Document doc) {
		NodeList nodeList = doc.getElementsByTagName(node);
		return nodeList.getLength();
	}

	public String retornaIDLote(String node, Document doc) {
		NodeList nodeList = doc.getElementsByTagName(node);
		return nodeList.item(0).getAttributes().getNamedItem("id").getNodeValue().toString();
	}

	// Verifica Tipo do documento
	private String obtemElementoAssinar(Document doc) {

		String tipoEvento = null;
		if (quantosElementosExistem("evtCadDeclarante", doc) != 0)
			tipoEvento = "evtCadDeclarante";
		else if (quantosElementosExistem("evtAberturaeFinanceira", doc) != 0)
			tipoEvento = "evtAberturaeFinanceira";
		else if (quantosElementosExistem("evtCadIntermediario", doc) != 0)
			tipoEvento = "evtCadIntermediario";
		else if (quantosElementosExistem("evtCadPatrocinado", doc) != 0)
			tipoEvento = "evtCadPatrocinado";
		else if (quantosElementosExistem("evtExclusaoeFinanceira", doc) != 0)
			tipoEvento = "evtExclusaoeFinanceira";
		else if (quantosElementosExistem("evtExclusao", doc) != 0)
			tipoEvento = "evtExclusao";
		else if (quantosElementosExistem("evtFechamentoeFinanceira", doc) != 0)
			tipoEvento = "evtFechamentoeFinanceira";
		else if (quantosElementosExistem("evtMovOpFin", doc) != 0)
			tipoEvento = "evtMovOpFin";
		else if (quantosElementosExistem("evtMovPP", doc) != 0)
			tipoEvento = "evtMovPP";

		return tipoEvento;
	}
	

	// Criado para assinar os xmls após a setar a informação do ID no Reference.
	private void assinarArquivo(KeyStore keystore, String alias, Component applet, Document doc, String tipoEvento) {

		try {

			// Pegar ID
			//String tipoEvento = obtemElementoAssinar(doc);
			Element el = (Element) doc.getElementsByTagName(tipoEvento).item(0);
			String id = el.getAttribute("id");
			el.setIdAttribute("id", true);
			// ------------

			PrivateKey chavePrivada = (PrivateKey) keystore.getKey(alias, null);
			DOMSignContext dsc = new DOMSignContext(chavePrivada, doc.getDocumentElement());

			// Alterado SHA1 para SHA256
			XMLSignatureFactory fabricaAssinatura = XMLSignatureFactory.getInstance("DOM");
			ArrayList<Transform> transformList = signatureFactory(fabricaAssinatura);

			Reference reference = fabricaAssinatura.newReference("#".concat(id),
					fabricaAssinatura.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", null), transformList, null, null);

			SignedInfo signedInfo = fabricaAssinatura.newSignedInfo(
					fabricaAssinatura.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
					fabricaAssinatura.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
					Collections.singletonList(reference));

			X509Certificate certificado = (X509Certificate) keystore.getCertificate(alias);

			KeyInfoFactory fabricaKeyInfo = fabricaAssinatura.getKeyInfoFactory();
			List<Serializable> x509Content = new ArrayList<Serializable>();
			x509Content.add(certificado);
			X509Data xData = fabricaKeyInfo.newX509Data(x509Content);
			KeyInfo keyInfo = fabricaKeyInfo.newKeyInfo(Collections.singletonList(xData));

			XMLSignature signature = fabricaAssinatura.newXMLSignature(signedInfo, keyInfo);

			signature.sign(dsc);

		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro1", JOptionPane.ERROR_MESSAGE);
		} catch (InvalidAlgorithmParameterException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro2", JOptionPane.ERROR_MESSAGE);
		} catch (UnrecoverableKeyException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro3", JOptionPane.ERROR_MESSAGE);
		} catch (KeyStoreException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro4", JOptionPane.ERROR_MESSAGE);
		} catch (MarshalException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro9", JOptionPane.ERROR_MESSAGE);
		} catch (XMLSignatureException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro13", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(applet, e, "Erro14", JOptionPane.ERROR_MESSAGE);
		}

	}

	private ArrayList<Transform> signatureFactory(XMLSignatureFactory signatureFactory) throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException {
		ArrayList<Transform> transformList = new ArrayList<Transform>();
		TransformParameterSpec tps = null;
		Transform envelopedTransform = signatureFactory.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", tps);
		Transform c14NTransform = signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", tps);

		transformList.add(envelopedTransform);
		transformList.add(c14NTransform);
		return transformList;
	}

	/**
	 * Método responsável pela envio dos arquivos criptografados e compactados para a Receita Federal.
	 * 
	 * @param eFinanceira
	 * @param ambiente
	 * @param applet
	 * @return String xmlRetorno
	 */
	public String enviaXmlCriptoZipado(EFinanceira eFinanceira, String ambiente, Applet applet, KeyStore keystore, String alias) {

		HttpURLConnection connection = null;
		StringBuilder xmlRetorno = new StringBuilder();
		BufferedReader retornoWs = null;

		try {

			// Define o url a ser usado de acordo com o ambiente de execução.
			URL url = null;
			if ("PROD".equals(ambiente)) {
				url = new URL(URL_PRODUCAO);
			} else {
				url = new URL(URL_PRE_PRODUCAO);
			}

			// Controi o xml com o envelope SOAP que será enviado para a receita.
			String soapEnvelope = ConstrutorXML.constroiXML(eFinanceira);

			// Configura o proxy da PREVI.
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ENDERECO_PROXY, PORTA_PROXY));

			// // Abre a conexão com o serviço.
			connection = (HttpURLConnection) url.openConnection(proxy);

			// Configura a conexão passando o certificado e a chave privada para permitir a conexão com o site da receita.
			PrivateKey chavePrivada = (PrivateKey) keystore.getKey(alias, null);
			X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
			TrustModifier.relaxHostChecking(connection, chavePrivada, certificate);

			connection.setRequestProperty("Content-type", "application/soap+xml; charset=utf-8");
			connection.setRequestProperty("Content-Length", String.valueOf(soapEnvelope.length()));
			connection.setRequestProperty("SOAPAction", "http://sped.fazenda.gov.br/ReceberLoteEventoCriptoGZip");

			// Define o método HTTP de envio.
			connection.setRequestMethod("POST");
			// Define que a Conexão terá uma saída/retorno.
			connection.setDoOutput(true);
			// Define que a Conexão terá uma entrada.
			connection.setDoInput(true);

			// Efetua o envio do XML para o serviço da Receita Federal.
			OutputStream reqStream = connection.getOutputStream();

			reqStream.write(soapEnvelope.getBytes());
			reqStream.flush();

			retornoWs = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line = null;

			while ((line = retornoWs.readLine()) != null) {
				xmlRetorno.append(line);
			}

		} catch (UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro14", JOptionPane.ERROR_MESSAGE);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro15", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro16", JOptionPane.ERROR_MESSAGE);
		} catch (KeyManagementException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro17", JOptionPane.ERROR_MESSAGE);
		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro18", JOptionPane.ERROR_MESSAGE);
		} catch (KeyStoreException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro19", JOptionPane.ERROR_MESSAGE);
		} catch (UnrecoverableKeyException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro20", JOptionPane.ERROR_MESSAGE);
		} catch (CertificateException e) {
			JOptionPane.showMessageDialog(applet, e, "Erro21", JOptionPane.ERROR_MESSAGE);
		} finally {
			if (retornoWs != null)
				try {
					retornoWs.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(applet, e, "Erro17", JOptionPane.ERROR_MESSAGE);
				}
			if (connection != null)
				connection.disconnect();
		}

		return xmlRetorno.toString();

	}

	private String retornaBanco(final Applet applet) {

		String result = null;
		try {
			DOMService service = DOMService.getService(applet);
			result = (String) service.invokeAndWait(new DOMAction() {
				public Object run(DOMAccessor accessor) {
					HTMLDocument doc = (HTMLDocument) accessor.getDocument(applet);
					HTMLCollection forms = doc.getForms();
					HTMLFormElement form = (HTMLFormElement) forms.namedItem("formCabecalho");
					HTMLCollection elements = form.getElements();
					int length = elements.getLength();
					for (int i = 0; i < length; i++) {
						Node node = elements.item(i);
						if (node instanceof HTMLInputElement) {
							HTMLInputElement element = (HTMLInputElement) node;
							if (element.getName().equalsIgnoreCase("dataBaseLogada"))
								return element.getValue();
						}
					}
					return "";
				}
			});
		} catch (DOMUnsupportedException e1) {
			JOptionPane.showMessageDialog(applet, e1, "Erro30", JOptionPane.ERROR_MESSAGE);
		} catch (DOMAccessException e2) {
			JOptionPane.showMessageDialog(applet, e2, "Erro31", JOptionPane.ERROR_MESSAGE);
		}
		return result;

	}

	private void retornaXmls(String xmlEnvia, String xmlRetorno, Applet applet, int numArqEnv, int numTotalArq) {

		JSObject window = JSObject.getWindow(applet);
		window.call("retornaXmls", new Object[] { removeHeaderXML(xmlEnvia), removeHeaderXML(xmlRetorno), numArqEnv, numTotalArq });

		if (numArqEnv == numTotalArq) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException exp) {
				exp.printStackTrace();
			}
		}

	}

	public static String removeHeaderXML(String xml) {
		if ((xml != null) && (!"".equals(xml))) {
			xml = xml.replaceAll("utf", "UTF");
			xml = xml.replaceAll(" standalone=\"no\"", "");
			xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
		}

		return xml;
	}

	public static String incluiHeaderXML(String xml) {
		String tmop = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		tmop = tmop + "<eFinanceira xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ";
		tmop = tmop + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
		tmop = tmop + "xmlns=\"http://www.eFinanceira.gov.br/schemas/envioLoteEventos/v1_2_0\">";
		tmop = tmop + "<loteEventos>";
		tmop = tmop + xml;
		tmop = tmop + "</loteEventos></eFinanceira>";

		return tmop;
	}

	// Método obrigatório para classes que extendem AbstractAppletExecute.
	@Override
	public void cancel(KeyStore arg0, String arg1, Applet arg2) {

	}

}