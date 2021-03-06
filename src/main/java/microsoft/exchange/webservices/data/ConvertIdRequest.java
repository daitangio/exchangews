/**************************************************************************
 * copyright file="ConvertIdRequest.java" company="Microsoft"
 *     Copyright (c) Microsoft Corporation.  All rights reserved.
 * 
 * Defines the ConvertIdRequest.java.
 **************************************************************************/
package microsoft.exchange.webservices.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

/**
 * Represents a ConvertId request.
 */
final class ConvertIdRequest extends
		MultiResponseServiceRequest<ConvertIdResponse> {

	/** The destination format. */
	private IdFormat destinationFormat = IdFormat.EwsId;

	/** The ids. */
	private List<AlternateIdBase> ids = new ArrayList<AlternateIdBase>();

	/**
	 * Initializes a new instance of the class.
	 * 
	 * @param service
	 *            the service
	 * @param errorHandlingMode
	 *            the error handling mode
	 * @throws Exception 
	 */
	protected ConvertIdRequest(ExchangeService service,
			ServiceErrorHandling errorHandlingMode)
			throws Exception {
		super(service, errorHandlingMode);
	}

	/**
	 * Initializes a new instance of the class.
	 * 
	 * @param service
	 *            the service
	 * @param responseIndex
	 *            the response index
	 * @return the convert id response
	 */
	@Override
	protected ConvertIdResponse createServiceResponse(ExchangeService service,
			int responseIndex) {
		return new ConvertIdResponse();
	}

	/**
	 * Gets the name of the response XML element.
	 * 
	 * @return XML element name.
	 */
	@Override
	protected String getResponseXmlElementName() {
		return XmlElementNames.ConvertIdResponse;
	}

	/**
	 * Gets the name of the response message XML element.
	 * 
	 * @return XML element name.
	 */
	@Override
	protected String getResponseMessageXmlElementName() {
		return XmlElementNames.ConvertIdResponseMessage;
	}

	/**
	 * Gets the expected response message count.
	 * 
	 * @return Number of expected response messages.
	 */
	@Override
	protected int getExpectedResponseMessageCount() {
		return this.ids.size();
	}

	/**
	 * Gets the name of the XML element.
	 * 
	 * @return XML element name.
	 */
	@Override
	protected String getXmlElementName() {
		return XmlElementNames.ConvertId;
	}

	/**
	 * Validate request.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	protected void validate() throws Exception {
		super.validate();
		EwsUtilities.validateParamCollection(this.ids.iterator(), "Ids");
	}

	/**
	 * Writes XML elements.
	 * 
	 * @param writer
	 *            the writer
	 * @throws XMLStreamException
	 *             the xML stream exception
	 * @throws ServiceXmlSerializationException
	 *             the service xml serialization exception
	 */
	@Override
	protected void writeElementsToXml(EwsServiceXmlWriter writer)
			throws XMLStreamException, ServiceXmlSerializationException {
		writer.writeAttributeValue(XmlAttributeNames.DestinationFormat,
				this.destinationFormat);
		writer.writeStartElement(XmlNamespace.Messages,
				XmlElementNames.SourceIds);
		for (AlternateIdBase alternateId : this.ids) {
			alternateId.writeToXml(writer);
		}

		writer.writeEndElement(); // SourceIds
	}

	/**
	 * Gets the request version.
	 * 
	 * @return Earliest Exchange version in which this request is supported.
	 */
	@Override
	protected ExchangeVersion getMinimumRequiredServerVersion() {
		return ExchangeVersion.Exchange2007_SP1;
	}

	/**
	 * Gets the destination format. <value>The destination
	 * format.</value>
	 * 
	 * @return the destination format
	 */
	public IdFormat getDestinationFormat() {
		return this.destinationFormat;
	}

	/**
	 * Sets the destination format.
	 * 
	 * @param destinationFormat
	 *            the new destination format
	 */
	public void setDestinationFormat(IdFormat destinationFormat) {
		this.destinationFormat = destinationFormat;
	}

	/**
	 * Gets the ids. <value>The ids.</value>
	 * 
	 * @return the ids
	 */
	public List<AlternateIdBase> getIds() {
		return this.ids;
	}
}
