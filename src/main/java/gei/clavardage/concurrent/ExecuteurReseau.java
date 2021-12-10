package gei.clavardage.concurrent;

import java.util.concurrent.Executors;

public class ExecuteurReseau extends Executeur {

	private static ExecuteurReseau instance;
	
	private ExecuteurReseau() {
		super(Executors.newCachedThreadPool());
	}
	
	public static ExecuteurReseau getInstance() {
		if (ExecuteurReseau.instance == null) {
			ExecuteurReseau.instance = new ExecuteurReseau();
		}
		return ExecuteurReseau.instance;
	}
}
