package no.hvl.dat110.iotsystem;

import no.hvl.dat110.client.Client;
import no.hvl.dat110.messages.Message;

public class DisplayDevice {
	
	private static final int COUNT = 10;
	public static int BROKERPORT = 8080;
	public static String BROKERHOST = "localhost";
	
	public static void main (String[] args) {
		
		System.out.println("Display starting ...");
		
		String user = "Display device";
		Client client = new Client(user, BROKERHOST, BROKERPORT);
		client.connect();
		client.createTopic("Temperature");
		client.subscribe("Temperature");
		for(int i = 0; i < COUNT; i++){
			Message message = client.receive();
			System.out.println(message);
		}
		
		client.disconnect();
		
		System.out.println("Display stopping ... ");
		
	}
}
