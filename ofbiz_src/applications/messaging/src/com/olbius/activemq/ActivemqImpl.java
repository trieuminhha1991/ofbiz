package com.olbius.activemq;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.ofbiz.base.util.Debug;

/**
 * @author Nguyen Ha
 *
 */
public class ActivemqImpl implements Activemq {

	private String user;
	private String pwd;
	private String url;
	private PooledConnectionFactory pcf;
	private int connection;
	
	private Map<String, MessageConsumer> consumers = new HashMap<String, MessageConsumer>();
	private Connection connectionListener;

	public ActivemqImpl(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.pwd = password;
		this.connection = 3;
	}

	protected Connection getConnection() throws JMSException {

		if (pcf == null) {
			if (url != null) {
				pcf = new PooledConnectionFactory(url);
				pcf.setMaxConnections(connection);
			} else {
				return null;
			}
		}

		if (user != null && pwd != null) {
			return pcf.createConnection(url, pwd);
		} else {
			return null;
		}
	}

	@Override
	public void sendMessage(OlbiusMessage message) {

		Connection connection = null;
		try {
			connection = getConnection();

			Session session = connection.createSession(message.isTransactionEnabled(), message.getAcknowledge());

			Destination dest = null;
			if (TOPIC.equals(message.getType())) {
				dest = session.createTopic(message.getDestination());
			} else {
				dest = session.createQueue(message.getDestination());
			}

			MessageProducer producer = session.createProducer(dest);

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			Message msg = null;

			if (message instanceof com.olbius.activemq.TextMessage) {
				msg = session.createTextMessage((String) message.getMessage());
			}

			if (msg != null) {
				producer.send(msg);
			}

		} catch (JMSException e) {
			Debug.logError(e, module);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					Debug.logError(e, module);
				}
			}
		}
	}

	@Override
	public void sendMessageTopic(OlbiusMessage message) {
		message.setType(TOPIC);
		sendMessage(message);
	}

	@Override
	public void sendMessageQueue(OlbiusMessage message) {
		message.setType(QUEUE);
		sendMessage(message);
	}

	@Override
	public void receiveMessage(MessageListener listener, OlbiusMessage message) {
		try {
			if (connectionListener == null) {
				connectionListener = getConnection();
				connectionListener.start();
			}

			MessageConsumer consumer = consumers.get(message.getDestination());

			if (consumer == null) {
				Session session = connectionListener.createSession(message.isTransactionEnabled(), message.getAcknowledge());

				Destination dest = null;
				if (TOPIC.equals(message.getType())) {
					dest = session.createTopic(message.getDestination());
				} else {
					dest = session.createQueue(message.getDestination());
				}

				consumer = session.createConsumer(dest);
				
				consumers.put(message.getDestination(), consumer);
			}
			
			consumer.setMessageListener(listener);
			
		} catch (JMSException e) {
			Debug.logError(e, module);
			return;
		}
	}

	@Override
	public void receiveMessageTopic(MessageListener listener, OlbiusMessage message) {
		message.setType(TOPIC);
		receiveMessage(listener, message);
	}

	@Override
	public void receiveMessageQueue(MessageListener listener, OlbiusMessage message) {
		message.setType(QUEUE);
		receiveMessage(listener, message);
	}

	@Override
	public void setMaxConnections(int connection) {
		this.connection = connection;
		if(pcf != null) {
			pcf.setMaxConnections(connection);
		}
	}

}
