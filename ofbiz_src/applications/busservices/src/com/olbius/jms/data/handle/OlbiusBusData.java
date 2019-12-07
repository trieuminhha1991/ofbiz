package com.olbius.jms.data.handle;

import java.util.List;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcherFactory;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.api.ActivemqSession;
import com.olbius.activemq.container.BusContainer;
import com.olbius.entity.cache.OlbiusCache;
import com.olbius.jms.data.JaxbConvert;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusData;
import com.olbius.jms.data.OlbiusJmsData;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.data.OlbiusJmsData.Insert;
import com.olbius.jms.event.OlbiusEvent;

public class OlbiusBusData implements OlbiusData {

	private Delegator delegator;
	private LocalDispatcher dispatcher;
	private GenericValue userLogin;

	public final static OlbiusCache<String> CACHE = new OlbiusCache<String>() {

		@Override
		public String loadCache(Delegator delegator, String key) throws Exception {

			List<GenericValue> values = delegator.findByAnd("BusConvert", UtilMisc.toMap("busId", key, "own", "Y"),
					null, false);

			if (values != null && !values.isEmpty()) {
				Notify notify = new Notify();
				notify.setOwnId(values.get(0).getString("ownId"));
				notify.setDataType(values.get(0).getString("type"));
				String party = values.get(0).getString("partyId");

				OlbiusMessage tmp = new OlbiusMessage();

				tmp.setType(OlbiusEvent.UPDATE_INFO);

				tmp.getDatas().add(notify);

				tmp.setUser(party);

				BusContainer.ACTIVEMQ_FACTORY.createProducer(delegator).sendMessage(
						BusContainer.ACTIVEMQ_FACTORY.getSend(), ActivemqSession.QUEUE,
						JaxbConvert.toString(new OlbiusBusConvert(delegator).bus(tmp, false)), party, null);

				return values.get(0).getString("busId");
			}

			return null;

		}

	};

	private final static OlbiusJmsData DATA = new OlbiusJmsData();

	static {
		DATA.load("BusData");
	}

	public OlbiusBusData(Delegator delegator) {
		this.delegator = delegator;
		this.dispatcher = new GenericDispatcherFactory().createLocalDispatcher("dispatcher", delegator);
		try {
			this.userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e) {
			BusContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusBusData.class.getName());
		}
	}

	@Override
	public void insert(MessageId id) throws Exception {

		Insert insert = DATA.getInsert(id.getDataType());

		if (insert != null) {
			insert.exc(id, dispatcher, delegator, userLogin, null);
		}

	}

}
