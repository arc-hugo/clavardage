package gei.clavardage.concurrent;

import java.util.concurrent.Executors;

public class ExecuteurFond extends Executeur {
	
	private static ExecuteurFond instance;
	
	private ExecuteurFond() {
		super(Executors.newSingleThreadExecutor());
	}
	
	public static ExecuteurFond getInstance() {
		if (ExecuteurFond.instance == null) {
			ExecuteurFond.instance = new ExecuteurFond();
		}
		return ExecuteurFond.instance;
	}

}
