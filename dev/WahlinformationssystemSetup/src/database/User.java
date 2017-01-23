package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

	private String name;
	private String password;
	private Map<Class<? extends DatabaseEntity>, List<UserPrivilege>> privileges;

	public User(String name, String password) {
		this.name = name;
		this.password = password;
		this.privileges = new HashMap<Class<? extends DatabaseEntity>, List<UserPrivilege>>();
	}

	public void addPrivilege(Class<? extends DatabaseEntity> entityClass,
			UserPrivilege privilege) {
		if (!this.privileges.containsKey(entityClass)) {
			this.privileges.put(entityClass, new ArrayList<UserPrivilege>());
		}
		this.privileges.get(entityClass).add(privilege);
	}

	public void addPrivilege(UserPrivilege privilege) {
		addPrivilege(DatabaseEntity.Any.class, privilege);
	}

	public String getName() {
		return name;
	}

	protected String getPassword() {
		return password;
	}

	public Map<Class<? extends DatabaseEntity>, List<UserPrivilege>> getPrivileges() {
		return privileges;
	}
}
