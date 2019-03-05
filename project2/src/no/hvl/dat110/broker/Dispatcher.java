package no.hvl.dat110.broker;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import no.hvl.dat110.common.Logger;
import no.hvl.dat110.common.Stopable;
import no.hvl.dat110.messages.*;
import no.hvl.dat110.messagetransport.Connection;

public class Dispatcher extends Stopable {

	private Storage storage;

	public Dispatcher(Storage storage) {
		super("Dispatcher");
		this.storage = storage;

	}

	@Override
	public void doProcess() {

		Collection<ClientSession> clients = storage.getSessions();

		Logger.lg(".");
		for (ClientSession client : clients) {

			Message msg = null;

			if (client.hasData()) {
				msg = client.receive();
			}

			if (msg != null) {
				dispatch(client, msg);
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void dispatch(ClientSession client, Message msg) {

		MessageType type = msg.getType();

		switch (type) {

		case DISCONNECT:
			onDisconnect((DisconnectMsg) msg);
			break;

		case CREATETOPIC:
			onCreateTopic((CreateTopicMsg) msg);
			break;

		case DELETETOPIC:
			onDeleteTopic((DeleteTopicMsg) msg);
			break;

		case SUBSCRIBE:
			onSubscribe((SubscribeMsg) msg);
			break;

		case UNSUBSCRIBE:
			onUnsubscribe((UnsubscribeMsg) msg);
			break;

		case PUBLISH:
			onPublish((PublishMsg) msg);
			break;

		default:
			Logger.log("broker dispatch - unhandled message type");
			break;

		}
	}

	// called from Broker after having established the underlying connection
	public void onConnect(ConnectMsg msg, Connection connection) {
		String user = msg.getUser();
		try {
			storage.addClientSession(user, connection);
			ClientSession onlineClient = storage.clients.get(user);
			if(storage.messageBuffer.get(user) != null) {
				for(Message message : storage.messageBuffer.get(user)) {
					onlineClient.send(message);
				}
			}
			Logger.log("onConnect:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	// called by dispatch upon receiving a disconnect message 
	public void onDisconnect(DisconnectMsg msg) {
		String user = msg.getUser();
		try {
			storage.messageBuffer.put(user, new ArrayList<Message>());
			storage.removeClientSession(user);
			Logger.log("onDisconnect:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void onCreateTopic(CreateTopicMsg msg) {
		try {
			storage.createTopic(msg.getTopic());
			Logger.log("onCreateTopic:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void onDeleteTopic(DeleteTopicMsg msg) {
		try {
			storage.deleteTopic(msg.getTopic());
			Logger.log("onDeleteTopic:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void onSubscribe(SubscribeMsg msg) {
		try {
			storage.addSubscriber(msg.getUser(), msg.getTopic());
			Logger.log("onSubscribe:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void onUnsubscribe(UnsubscribeMsg msg) {
		try {
			storage.removeSubscriber(msg.getUser(), msg.getTopic());
			Logger.log("onUnsubscribe:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void onPublish(PublishMsg msg) {
		try {
			Set<String> subscribers = storage.getSubscribers(msg.getTopic());
			for(String findUser : subscribers) {
				ClientSession session = storage.getSession(findUser);
				if(session != null) {
					session.send(msg);
				}else {
					storage.messageBuffer.get(findUser).add(msg);
				}
			}
			Logger.log("onPublish:" + msg.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
