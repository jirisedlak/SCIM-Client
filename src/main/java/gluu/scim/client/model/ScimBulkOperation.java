/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * SCIM Bulk operation
 *
 * @author Reda Zerrad Date: 04.18.2012
 */

@XmlRootElement(name="Bulk")
@XmlAccessorType(XmlAccessType.NONE)
@JsonPropertyOrder({"schemas","Operations"})
public class ScimBulkOperation  {

	
	private List<String> schemas;
	@JsonProperty
	private List<BulkRequests> Operations;
	
	public ScimBulkOperation(){
		schemas = new ArrayList<String>();
		Operations = new ArrayList<BulkRequests>();
	}
	
	public List<String> getSchemas() {
		
		return this.schemas;
	}
	
	public void setSchemas(List<String> schemas){
		this.schemas = schemas;
	}
	@JsonIgnore
	@XmlElementWrapper(name="Operations")
    @XmlElement(name="operation")
public List<BulkRequests> getOperations() {
		
		return this.Operations;
	}
	
	public void setOperations(List<BulkRequests> Operations ){
		this.Operations = Operations;
	}
}
