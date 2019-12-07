package com.olbius.catalina.container;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.ofbiz.base.util.Debug;
import org.ofbiz.catalina.container.OfbizStore;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * @author Nguyen Ha
 *
 */
public class OlbiusStore extends OfbizStore {

	public OlbiusStore(Delegator delegator) {
		super(delegator);
	}

	@Override
	public void save(Session session) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));

		((StandardSession) session).writeObjectData(oos);
		oos.close();
		oos = null;

		byte[] obs = bos.toByteArray();
		long size = obs.length;

		GenericValue sessionValue = delegator.makeValue(entityName);
		sessionValue.setBytes("sessionInfo", obs);
		sessionValue.set("sessionId", session.getId());
		sessionValue.set("sessionSize", size);
		sessionValue.set("isValid", session.isValid() ? "Y" : "N");
		sessionValue.set("maxIdle", (long)session.getMaxInactiveInterval());
		sessionValue.set("lastAccessed", session.getLastAccessedTime());

		try {
			delegator.createOrStore(sessionValue);
		} catch (GenericEntityException e) {
			throw new IOException(e.getMessage());
		}

		Debug.logInfo("Persisted session [" + session.getId() + "]", module);
	}

}
