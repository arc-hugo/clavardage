package gei.clavardage.concurrent;

import java.util.concurrent.Executors;

public class ExecuteurSession extends Executeur {

	private static ExecuteurSession instance;
	
	private ExecuteurSession() {
		super(Executors.newCachedThreadPool());
	}
	
	public static ExecuteurSession getInstance() {
		if (ExecuteurSession.instance == null) {
			ExecuteurSession.instance = new ExecuteurSession();
		}
		return ExecuteurSession.instance;
	}

}
