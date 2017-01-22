package database;

import java.sql.SQLException;
import java.util.ArrayList;

import util.Pair;

@SuppressWarnings("serial")
public class EntityCollection extends ArrayList<DatabaseEntity> {

	private Class<? extends DatabaseEntity> cls;

	public EntityCollection(Class<? extends DatabaseEntity> cls) {
		this.cls = cls;
	}

	public Class<? extends DatabaseEntity> getEntityType() {
		return cls;
	}

	public boolean addDistinct(DatabaseEntity entity) {
		if (entity != null && !contains(entity)) {
			return super.add(entity);
		}
		return false;
	}

	@SafeVarargs
	public final DatabaseEntity findFirst(
			Pair<String, ?>... attributeNamesAndValues) {
		for (DatabaseEntity entity : this) {
			boolean match = true;
			for (Pair<String, ?> pair : attributeNamesAndValues) {
				if (entity.getAttribute(pair.first) == null
						&& pair.second != null
						|| pair.second == null
						&& entity.getAttribute(pair.first) != null
						|| !entity.getAttribute(pair.first).getValue()
								.equals(pair.second)) {
					match = false;
					break;
				}
			}
			if (match) {
				return entity;
			}
		}
		return null;
	}

	public final void commitAll() throws SQLException {
		PostgreSQLDatabase.getCurrent().insertEntities(cls,
				this.toArray(new DatabaseEntity[0]));
	}
}
