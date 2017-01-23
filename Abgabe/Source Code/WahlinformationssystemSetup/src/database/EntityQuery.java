package database;

import java.sql.ResultSet;
import java.sql.SQLException;

import util.Pair;

public class EntityQuery {

	private String whereClause;
	private Class<? extends DatabaseEntity> cls;

	protected EntityQuery() {
	}

	public EntityQuery from(Class<? extends DatabaseEntity> cls) {
		if (cls != null) {
			this.cls = cls;
		}
		return this;
	}

	@SafeVarargs
	public final EntityQuery where(Pair<String, ?>... attrs) {
		try {
			if (attrs != null && attrs.length > 0) {
				StringBuilder builder = new StringBuilder(" where ");
				DatabaseEntity instance;

				instance = cls.newInstance();

				for (int idx = 0; idx < attrs.length;) {
					DatabaseAttribute attribute = instance
							.getAttribute(attrs[idx].first);
					Object value = attrs[idx].second;
					if (attribute.getIsForeignKey().second != null) {
						value = ((DatabaseEntity) value).getAttribute(
								attribute.getIsForeignKey().second).getValue();
					}
					String args = value != null ? "'" + value + "'" : "null";
					builder.append(attrs[idx].first).append("=").append(args);

					if (++idx < attrs.length) {
						builder.append(" and ");
					}
				}
				whereClause = builder.toString();
			}
		} catch (Exception e) {
			throw new RuntimeException("Couldn't resolve where clause!");
		}
		return this;
	}

	public DatabaseEntity execute() throws SQLException {
		ResultSet resultSet = PostgreSQLDatabase.getCurrent().executeQuery(
				this);

		try {
			if (resultSet != null && resultSet.next()) {
				DatabaseEntity entity = cls.newInstance();
				for (int idx = 1; idx <= resultSet.getMetaData()
						.getColumnCount(); idx++) {
					DatabaseAttribute attribute = entity.getAttribute(resultSet
							.getMetaData().getColumnName(idx));
					attribute.setValue(resultSet.getObject(idx));
					if (attribute.getIsForeignKey().second != null) {
						DatabaseEntity newValue = new EntityQuery()
								.from(attribute.getIsForeignKey().first)
								.where(new Pair<String, Object>(attribute
										.getIsForeignKey().second, attribute
										.getValue())).execute();
						attribute.setValue(newValue);
					}
				}
				return entity;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(
					"A DatabaseEntity has to provide an empty constructor initializing its DatabaseAttributes!");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"A DatabaseEntity has to provide an empty constructor initializing its DatabaseAttributes!");
		} finally {
			resultSet.getStatement().close();
		}
		return null;
	}

	protected String toSqlQuery() {
		return "select * from " + cls.getSimpleName().toLowerCase()
				+ whereClause + ";";
	}
}
