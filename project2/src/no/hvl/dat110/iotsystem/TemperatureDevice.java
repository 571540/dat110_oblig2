package no.hvl.dat110.iotsystem;

import no.hvl.dat110.client.Client;

public class TemperatureDevice {
	
	private static final int COUNT = 10;
	public static String TEMPTOPIC = "Temperature";
	public static int BROKERPORT = 8080;
	public static String BROKERHOST = "localhost";
	
	public static void main(String[] args) {
		
		TemperatureSensor sn = new TemperatureSensor();
		String user = "Temperature device";
		Client client = new Client(user, BROKERHOST, BROKERPORT);
		
		client.connect();
		
		for(int i = 0; i < COUNT; i++) {
			String message = Integer.toString(sn.read());
			client.publish(TEMPTOPIC, message);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		client.disconnect();

		System.out.println("Temperature device stopping ... ");
	}
}
