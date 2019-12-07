package com.olbius.jms.data.handle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.activemq.container.BusContainer;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.OlbiusMessage;

public class OlbiusBusConvert {

	private Delegator delegator;
	private OlbiusBusData busData;
	private boolean update;

	public OlbiusBusConvert(Delegator delegator) {
		this.delegator = delegator;
		this.busData = OlbiusBusDataFactory.getInstance(delegator);
		this.update = false;
	}

	public OlbiusBusConvert setUpdate(boolean update) {
		this.update = update;
		return this;
	}

	public OlbiusMessage bus(OlbiusMessage data, boolean busFlag) {
		while (data.getDatas().contains(null)) {
			data.getDatas().remove(null);
		}
		for (MessageData mesData : data.getDatas()) {
			if (mesData instanceof MessageId) {
				bus((MessageId) mesData, data.getUser(), busFlag);
			} else {
				busMethod(mesData, data.getUser(), busFlag);
			}
		}
		return data;
	}

	public void bus(MessageId id, String party, boolean busFlag) {

		if (id == null) {
			return;
		}

		try {
			getId(id, party, busFlag);
		} catch (GenericEntityException e) {
			BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusConvert.class.getName());
		}

		busMethod(id, party, busFlag);

	}

	@SuppressWarnings("rawtypes")
	private void busMethod(Object id, String party, boolean busFlag) {

		if (id == null) {
			return;
		}
		
		for (Method method : id.getClass().getMethods()) {
			if (MessageId.class.isAssignableFrom(method.getReturnType())) {

				try {
					MessageId tmp = (MessageId) method.invoke(id);
					bus(tmp, party, busFlag);
				} catch (Exception e) {
					BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusConvert.class.getName());
				}

			} else if (ArrayList.class.isAssignableFrom(method.getReturnType())) {

				try {
					ArrayList tmp = (ArrayList) method.invoke(id);
					while (tmp.contains(null)) {
						tmp.remove(null);
					}
					for (Object object : tmp) {
						if (object instanceof MessageId) {
							bus((MessageId) object, party, busFlag);
						} else {
							busMethod(object, party, busFlag);
						}
					}

				} catch (Exception e) {
					BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusConvert.class.getName());
				}

			} else if (MessageData.class.isAssignableFrom(method.getReturnType())) {
				try {
					MessageData tmp = (MessageData) method.invoke(id);
					busMethod(tmp, party, busFlag);
				} catch (Exception e) {
					BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusConvert.class.getName());
				}
			}
		}

		if (busFlag && id instanceof MessageId && "Y".equals(((MessageId) id).getOwnParty()) && !((MessageId) id).getDataType().contains("Confirm")) {
			try {
				busData.insert((MessageId)id);
			} catch (Exception e) {
				BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusConvert.class.getName());
			}
		}

	}

	public void getId(MessageId id, String party, boolean busFlag) throws GenericEntityException {

		if (busFlag) {

			if (id.getBusId() != null) {
				if (id.getOwnId() != null && id.isUpdate()) {
					updateId(id, party);
				}
				return;
			}

			if (id.getOwnId() == null) {
				
				/*if(id.getBusId() == null) {
					String busId = UUID.randomUUID().toString().replaceAll("-", "");
					GenericValue value = delegator.makeValue("BusConvert");
					value.set("busId", busId);
					value.set("ownId", id.getOwnId());
					value.set("type", id.getDataType());
					value.set("partyId", party);
					value.set("own", "Y");
					value.create();
					id.setOwnParty("Y");
					id.setBusId(busId);
				}
				*/
				return;
			}

			List<GenericValue> list = delegator.findByAnd("BusConvert",
					UtilMisc.toMap("ownId", id.getOwnId(), "type", id.getDataType(), "partyId", party), null, false);

			String busId;

			if (!list.isEmpty()) {
				busId = list.get(0).getString("busId");
				id.setOwnParty(list.get(0).getString("own"));
			} else {
				busId = UUID.randomUUID().toString().replaceAll("-", "");
				GenericValue value = delegator.makeValue("BusConvert");
				value.set("busId", busId);
				value.set("ownId", id.getOwnId());
				value.set("type", id.getDataType());
				value.set("partyId", party);
				value.set("own", "Y");
				value.create();
				id.setOwnParty("Y");
			}

			id.setBusId(busId);

		} else {

			if (id.getBusId() == null) {
				return;
			}

			List<GenericValue> list = delegator.findByAnd("BusConvert",
					UtilMisc.toMap("busId", id.getBusId(), "type", id.getDataType(), "partyId", party), null, false);

			if (!list.isEmpty()) {
				String ownId = list.get(0).getString("ownId");
				id.setOwnId(ownId);
				id.setOwnParty(list.get(0).getString("own"));
				id.setUpdate(update);
			} else {
				id.setOwnId(null);
				id.setOwnParty(null);
			}
		}

	}

	public void updateId(MessageId id, String party) throws GenericEntityException {

		if (id.getBusId() == null || "Y".equals(id.getOwnParty())) {
			return;
		}

		List<GenericValue> list = delegator.findByAnd("BusConvert",
				UtilMisc.toMap("busId", id.getBusId(), "type", id.getDataType(), "partyId", party), null, false);

		if (list != null && id.getOwnId() != null) {
			GenericValue value = delegator.makeValue("BusConvert");
			value.set("busId", id.getBusId());
			value.set("ownId", id.getOwnId());
			value.set("type", id.getDataType());
			value.set("partyId", party);
			value.set("own", "N");
			delegator.createOrStore(value);
		}

	}

}
