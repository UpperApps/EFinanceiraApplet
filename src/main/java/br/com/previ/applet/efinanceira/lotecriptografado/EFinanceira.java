//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.02.16 às 10:46:02 AM BRST 
//


package src.main.java.br.com.previ.applet.efinanceira.lotecriptografado;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loteCriptografado">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="idCertificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="chave" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="lote" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "loteCriptografado"
})
@XmlRootElement(name = "eFinanceira")
public class EFinanceira {

    @XmlElement(required = true)
    protected EFinanceira.LoteCriptografado loteCriptografado;

    /**
     * Obtém o valor da propriedade loteCriptografado.
     * 
     * @return
     *     possible object is
     *     {@link EFinanceira.LoteCriptografado }
     *     
     */
    public EFinanceira.LoteCriptografado getLoteCriptografado() {
        return loteCriptografado;
    }

    /**
     * Define o valor da propriedade loteCriptografado.
     * 
     * @param value
     *     allowed object is
     *     {@link EFinanceira.LoteCriptografado }
     *     
     */
    public void setLoteCriptografado(EFinanceira.LoteCriptografado value) {
        this.loteCriptografado = value;
    }


    /**
     * <p>Classe Java de anonymous complex type.
     * 
     * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="idCertificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="chave" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="lote" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id",
        "idCertificado",
        "chave",
        "lote"
    })
    public static class LoteCriptografado {

        @XmlElement(required = true)
        protected String id;
        @XmlElement(required = true)
        protected String idCertificado;
        @XmlElement(required = true)
        protected String chave;
        @XmlElement(required = true)
        protected String lote;

        /**
         * Obtém o valor da propriedade id.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Define o valor da propriedade id.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Obtém o valor da propriedade idCertificado.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdCertificado() {
            return idCertificado;
        }

        /**
         * Define o valor da propriedade idCertificado.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdCertificado(String value) {
            this.idCertificado = value;
        }

        /**
         * Obtém o valor da propriedade chave.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getChave() {
            return chave;
        }

        /**
         * Define o valor da propriedade chave.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setChave(String value) {
            this.chave = value;
        }

        /**
         * Obtém o valor da propriedade lote.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLote() {
            return lote;
        }

        /**
         * Define o valor da propriedade lote.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLote(String value) {
            this.lote = value;
        }

    }

}
