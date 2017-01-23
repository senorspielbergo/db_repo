package main;

import database.User;
import database.UserPrivilege;

public class SetupOptions {

	private boolean dropOldEntityClasses = true;
	private int maxCollectionSize = Integer.MAX_VALUE;
	private boolean insertStimmzettelRange = false;
	private int stimmzettelRangeFrom = 1;
	private int stimmzettelRangeTo = 299;
	private String user;
	private String password;
	private boolean showHelp;

	public static SetupOptions generate(String[] programArgs) {
		SetupOptions result = new SetupOptions();

		if (programArgs.length == 0) {
			throw new RuntimeException("Invalid number of program arguments!");
		} else if (programArgs[0].contains("-help")
				|| programArgs[1].contains("-help")) {
			result.showHelp = true;
			return result;
		}

		result.user = programArgs[0];
		result.password = programArgs[1];

		for (int idx = 2; idx < programArgs.length; idx++) {
			if (programArgs[idx].equals("-nodrop")) {
				result.dropOldEntityClasses = false;
			} else if (programArgs[idx].equals("-help")) {
				result.showHelp = true;
			} else if (programArgs[idx].equals("-srange")) {
				result.dropOldEntityClasses = false;
				result.insertStimmzettelRange = true;
				if (idx + 2 >= programArgs.length) {
					throw new RuntimeException(
							"Invalid number of arguments for option '-srange'!");
				}
				result.stimmzettelRangeFrom = Integer
						.valueOf(programArgs[++idx]);
				result.stimmzettelRangeTo = Integer.valueOf(programArgs[++idx]);
			} else if (programArgs[idx].equals("-max")) {
				if (idx + 1 >= programArgs.length) {
					throw new RuntimeException(
							"Invalid number of arguments for option '-srange'!");
				}
				result.maxCollectionSize = Integer.valueOf(programArgs[++idx]);
			} else {
				throw new RuntimeException("Invalid program argument: '"
						+ programArgs[idx] + "'!");
			}
		}

		return result;
	}

	public boolean showHelp() {
		return showHelp;
	}

	public User getUser() {
		User user = new User(this.user, this.password);
		user.addPrivilege(UserPrivilege.ALL);
		return user;
	}

	public String getPassword() {
		return password;
	}

	public boolean dropOldEntityClasses() {
		return dropOldEntityClasses;
	}

	public int getMaxCollectionSize() {
		return maxCollectionSize;
	}

	public boolean insertStimmzettelRange() {
		return insertStimmzettelRange;
	}

	public int getStimmzettelRangeFrom() {
		return stimmzettelRangeFrom;
	}

	public int getStimmzettelRangeTo() {
		return stimmzettelRangeTo;
	}
}
