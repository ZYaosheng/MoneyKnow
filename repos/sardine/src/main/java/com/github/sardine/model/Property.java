//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.4-10/27/2009 06:09 PM(mockbuild)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.23 at 06:27:19 PM PST 
//


package com.github.sardine.model;

import org.w3c.dom.Element;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;



/**
 * <p>Java class for anonymous complex type.</p>
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element ref="{DAV:}creationdate" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}displayname" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}getcontentlanguage" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}getcontentlength" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}getcontenttype" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}getetag" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}getlastmodified" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}lockdiscovery" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}resourcetype" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}supportedlock" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}owner" minOccurs="0"/&gt;  &lt;!-- (for DAV:acl) --&gt;
 *         &lt;any/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "property")
public class Property {


	@XmlAnyElement
	private Element property;

	public Element getProperty() {
		return property;
	}

	public void setProperty(Element property) {
		this.property = property;
	}

}
