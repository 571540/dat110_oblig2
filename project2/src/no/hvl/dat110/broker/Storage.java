package no.hvl.dat110.broker;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
import no.hvl.dat110.common.Logger;
import no.hvl.dat110.messages.Message;
import no.hvl.dat110.messagetransport.Connection;

public class Storage {

	protected ConcurrentHashMap<String, Set<String>> subscriptions;
	protected ConcurrentHashMap<String, ClientSession> clients;
	protected ConcurrentHashMap<String, List<Message>> messageBuffer;

	public Storage() {
		subscriptions = new ConcurrentHashMap<String, Set<String>>();
		clients = new ConcurrentHashMap<String, ClientSession>();
		messageBuffer = new ConcurrentHashMap<String, List<Message>>();
	}

	public Collection<ClientSession> getSessions() {
		return clients.values();
	}

	public Set<String> getTopics() {

		return subscriptions.keySet();

	}

	public ClientSession getSession(String user) {
		ClientSession session = clients.get(user);
		return session;
	}

	public Set<String> getSubscribers(String topic) {
		return (subscriptions.get(topic));
	}

	public void addClientSession(String user, Connection connection) {
		try {
			ClientSession addSession = new ClientSession(user, connection);
			clients.put(user, addSession);
		} catch (Exception e) {
			System.out.println("Failed to add new client session.");
			e.printStackTrace();
		}
	}

	public void removeClientSession(String user) {
		if(clients.containsKey(user)) {
			clients.remove(user);	
		}else {
			System.out.println("This client does not exist, client session removal failed.");
		}
	}

	public void createTopic(String topic) {
		if(!subscriptions.containsKey(topic)) {
			Set<String> subscribers = new HashSet<String>();
			subscriptions.put(topic, subscribers);
		}else {
			System.out.println("This topic already exists.");
		}
	}

	public void deleteTopic(String topic) {
		if(subscriptions.containsKey(topic)){
			subscriptions.remove(topic);
		}else {
			System.out.println("No such topic exists.");
		}
	}

	public void addSubscriber(String user, String topic) {
		Set<String> updateSet = getSubscribers(topic);
		updateSet.add(user);
		subscriptions.put(topic, updateSet);
	}

	public void removeSubscriber(String user, String topic) {
		Set<String> updateSet = getSubscribers(topic);
		updateSet.remove(user);
		subscriptions.put(topic, updateSet);
	}
}
