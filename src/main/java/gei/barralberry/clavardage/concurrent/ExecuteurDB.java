package gei.barralberry.clavardage.concurrent;

import java.util.concurrent.Executors;

public class ExecuteurDB extends Executeur {

	private static ExecuteurDB instance;

	public ExecuteurDB() {
		super(Executors.newSingleThreadExecutor());
	}

	public static ExecuteurDB getInstance() {
		if (ExecuteurDB.instance == null) {
			ExecuteurDB.instance = new ExecuteurDB();
		}
		return ExecuteurDB.instance;
	}
}
