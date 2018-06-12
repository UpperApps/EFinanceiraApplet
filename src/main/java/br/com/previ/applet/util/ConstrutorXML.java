package src.main.java.br.com.previ.applet.util;

import src.main.java.br.com.previ.applet.efinanceira.lotecriptografado.EFinanceira;

/**
 * Constroi o xml do envelope SOAP a ser enviado para a Receita Federal.
 * 
 * @author Rodrigo Melo
 * 
 */
public final class ConstrutorXML {

	/**
	 * Constroi xmls.
	 * 
	 * @param list
	 *            list
	 * @return String constroiXML
	 * @throws NegocioException
	 * 
	 */
	public static String constroiXML(EFinanceira efinanceira) {

		try {

			StringBuilder soapEnvelope = new StringBuilder();
			StringBuilder loteEvento = new StringBuilder();
			
			// Converte o lote a ser enviado em Base64 com charset UTF-8.
			loteEvento.append("<eFinanceira xmlns=\"http://www.eFinanceira.gov.br/schemas/envioLoteCriptografado/v1_2_0\">");
			loteEvento.append("<loteCriptografado>");
			loteEvento.append("<id>");
			loteEvento.append(efinanceira.getLoteCriptografado().getId());
			loteEvento.append("</id>");
			loteEvento.append("<idCertificado>");
			loteEvento.append(efinanceira.getLoteCriptografado().getIdCertificado());
			loteEvento.append("</idCertificado>");
			loteEvento.append("<chave>");
			loteEvento.append(efinanceira.getLoteCriptografado().getChave());
			loteEvento.append("</chave>");
			loteEvento.append("<lote>");
			loteEvento.append(efinanceira.getLoteCriptografado().getLote());
			loteEvento.append("</lote>");
			loteEvento.append("</loteCriptografado>");
			loteEvento.append("</eFinanceira>");

			String loteEventoBase64 = Converter.convertToBase64(loteEvento.toString(), "UTF-8");

			// Monta o envelope SOAP que será enviado para a Receita Federal.
			soapEnvelope.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapEnvelope.append("<soap12:Envelope");
			soapEnvelope.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			soapEnvelope.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
			soapEnvelope.append(" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
			soapEnvelope.append("<soap12:Header/>");
			soapEnvelope.append("<soap12:Body>");

			soapEnvelope.append("<bufferXmlComLoteCriptografadoGZip xmlns=\"http://sped.fazenda.gov.br/\">" + loteEventoBase64
					+ "</bufferXmlComLoteCriptografadoGZip>");

			soapEnvelope.append("</soap12:Body>");
			soapEnvelope.append("</soap12:Envelope>");

			return soapEnvelope.toString();

		} catch (Exception e) {
			throw new RuntimeException("Erro ao construir xml!", e);
		}

	}

}
