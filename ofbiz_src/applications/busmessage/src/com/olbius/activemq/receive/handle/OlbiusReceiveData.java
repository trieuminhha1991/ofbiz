package com.olbius.activemq.receive.handle;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcherFactory;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.activemq.container.MessageContainer;
import com.olbius.entity.cache.OlbiusCache;
import com.olbius.jms.data.MessageId;
import com.olbius.jms.data.Notify;
import com.olbius.jms.data.OlbiusData;
import com.olbius.jms.data.OlbiusJmsData;
import com.olbius.jms.data.OlbiusJmsData.Callback;
import com.olbius.jms.data.OlbiusJmsData.Insert;
import com.olbius.jms.data.OlbiusMessage;
import com.olbius.jms.event.OlbiusEvent;

public class OlbiusReceiveData implements OlbiusData{

	private Delegator delegator;
	private LocalDispatcher dispatcher;
	private GenericValue userLogin;

	public final static String BUS_CODE = UtilProperties.getPropertyValue("BusCode", "CODE", "0084x365");
	
	public final static OlbiusCache<String> CACHE = new OlbiusCache<String>() {

		@Override
		public String loadCache(Delegator delegator, String key) throws Exception {
			String tmp = key.substring(key.indexOf("#") + 1);
			return delegator.getNextSeqId(tmp);
		}
		
	};
	
	private final static OlbiusJmsData DATA = new OlbiusJmsData();
	
	private final static Callback UPDATE_ID = new Callback() {
		
		@Override
		public Object run(MessageId message, LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin) {
			
			if("Y".equals(message.getOwnParty())) {
				return null;
			}
			
			OlbiusMessage messeData = new OlbiusMessage();

			messeData.setType(OlbiusEvent.UPDATE_ID);

			Notify notify = new Notify();

			notify.setBusId(message.getBusId());

			notify.setOwnId(message.getOwnId());

			notify.setDataType(message.getDataType());

			notify.setUpdate(true);

			messeData.getDatas().add(notify);

			MessageContainer.EVENT_FACTORY.getSendEvent(delegator).send(messeData);
			
			return null;
		}
	};
	
	static {
		DATA.load("ReceiveData");
	}

	public OlbiusReceiveData(Delegator delegator) {
		this.delegator = delegator;
		this.dispatcher = new GenericDispatcherFactory().createLocalDispatcher("dispatcher", delegator);
		try {
			this.userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e) {
			MessageContainer.ACTIVEMQ_FACTORY.getHandleError().handle(e, OlbiusReceiveData.class.getName());
		}
	}

	@Override
	public void insert(MessageId id) throws Exception {
		
		Insert insert = DATA.getInsert(id.getDataType());
		
		if(insert != null) {
			insert.exc(id, dispatcher, delegator, userLogin, UPDATE_ID);
		}
		
	}
	
}
