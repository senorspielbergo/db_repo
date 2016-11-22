/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author theralph
 */
public class SqlRunner {
	private Connection connection = null;
	private final ConcurrentLinkedQueue<String> pendingCommands = new ConcurrentLinkedQueue<String>();
	private boolean forceDeath = false;

	private static SqlRunner instance = null;

	public static SqlRunner instance() {
		if (instance == null) {
			instance = new SqlRunner();
		}
		return instance;
	}

	public SqlRunner() {
		connection = DatabaseConnectionManager.getInstance().getConnection();
		new Thread(() -> {
			do {
				try {
					Thread.sleep(200);
					if (!pendingCommands.isEmpty()) {
						Statement statement = connection.createStatement();
						statement.execute(pendingCommands.poll());
						statement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (!forceDeath);
		});// .start();
	}

	public void runScript(InputStream scriptInputStream) throws SQLException,
			IOException {
		try (BufferedReader scriptReader = new BufferedReader(
				new InputStreamReader(scriptInputStream))) {
			String line = null;

			while ((line = scriptReader.readLine()) != null) {
				execute(line);
			}
		}
	}

	public void execute(String line) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute(line);
		statement.close();
		// pendingCommands.offer(line);
	}

	public void close() {
		forceDeath = true;
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
