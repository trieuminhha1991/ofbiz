package com.olbius.activemq.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntitySaxReader;
import org.xml.sax.SAXException;

import javolution.xml.sax.XMLReaderImpl;

/**
 * @author Nguyen Ha
 *
 */
public class EntityReader extends EntitySaxReader {

	public EntityReader(Delegator delegator, int transactionTimeout) {
		super(delegator, transactionTimeout);
	}

	public EntityReader(Delegator delegator) {
		super(delegator);
	}

	public List<GenericValue> getGenericValues(String content) throws SAXException, IOException {

		if (content == null) {
			Debug.logWarning("content was null, doing nothing", module);
			return null;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes("UTF-8"));

		return getGenericValues(bis);
	}

	public List<GenericValue> getGenericValues(InputStream is) throws SAXException, java.io.IOException {

		XMLReaderImpl parser = new XMLReaderImpl();

		parser.setContentHandler(this);
		parser.setErrorHandler(this);

		try {
			parser.parse(is);

		} catch (Exception e) {
			Debug.logError(e, module);
			throw new SAXException("A transaction error occurred reading data", e);
		}
		if (!valuesToWrite.isEmpty()) {
			return valuesToWrite;
		} else {
			return null;
		}
	}

}
