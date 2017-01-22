package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLDatabase {

	private static final String POSTGRES_DRIVER = "org.postgresql.Driver";
	private static final String DB_NAME = "wahlinfo_db";
	private static final String DB_PATH = "jdbc:postgresql://localhost:5432/"
			+ DB_NAME;

	private Connection connection;
	private List<Table> tables;

	private static PostgreSQLDatabase instance;

	private PostgreSQLDatabase() {
		this.tables = new ArrayList<Table>();

	}

	public static void init(User user) {
		instance = new PostgreSQLDatabase();
		try {
			Class.forName(POSTGRES_DRIVER);
			instance.connection = DriverManager.getConnection(DB_PATH,
					user.getName(), user.getPassword());

		} catch (Exception e) {
			throw new RuntimeException("Couldn't access postgreSQL database!");
		}
	}

	public static PostgreSQLDatabase getCurrent() {
		return instance;
	}

	public void createEntityClass(Class<? extends DatabaseEntity> entityClass)
			throws SQLException {
		try {
			DatabaseEntity entity = entityClass.newInstance();
			List<Column> columns = new ArrayList<Column>();
			List<DatabaseAttribute> attributes = entity.getAttributes();

			String tableName = entityClass.getSimpleName().toLowerCase();

			for (DatabaseAttribute attribute : attributes) {
				columns.add(attribute.toColumn());
			}

			Table table = new Table(tableName, columns.toArray(new Column[0]));

			System.out.println("Creating table (if not exists): "
					+ table.getSqlDescription() + "...");

			tables.add(table);

			StringBuilder builder = new StringBuilder(
					"create table if not exists ").append(table
					.getSqlDescription());
			execute(builder.toString());

		} catch (InstantiationException e) {
			throw new RuntimeException(
					"A DatabaseEntity has to provide an empty constructor initializing its DatabaseAttributes!");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"A DatabaseEntity has to provide an empty constructor initializing its DatabaseAttributes!");
		}
	}

	public void dropEntityClass(Class<? extends DatabaseEntity> entityClass)
			throws SQLException {
		String tableName = entityClass.getSimpleName().toLowerCase();
		System.out.println("Dropping table: " + tableName + "...");
		StringBuilder builder = new StringBuilder("drop table if exists ")
				.append(tableName).append(";");
		execute(builder.toString());
	}

	public EntityQuery newQuery() {
		return new EntityQuery();
	}

	protected ResultSet executeQuery(EntityQuery query) throws SQLException {
		Statement statement = connection.createStatement();
		String sqlString = query.toSqlQuery();
		ResultSet result = statement.executeQuery(sqlString);
		return result;
	}

	private void execute(String sqlString) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute(sqlString);
		statement.close();
	}

	public void createIndex(Class<? extends DatabaseEntity> cls,
			String indexAttribute) throws SQLException {
		String className = cls.getSimpleName().toLowerCase();
		System.out.println("Creating index " + indexAttribute + " on table "
				+ className + "...");
		StringBuilder builder = new StringBuilder("create index ")
				.append(className + "_" + indexAttribute + "_idx ")
				.append("on ").append(className).append(" (")
				.append(indexAttribute).append(");");
		execute(builder.toString());
	}

	public void dropIndex(Class<? extends DatabaseEntity> cls,
			String indexAttribute) throws SQLException {
		String className = cls.getSimpleName().toLowerCase();
		System.out.println("Dropping index " + indexAttribute + " on table "
				+ className + "...");
		StringBuilder builder = new StringBuilder("drop index if exists ")
				.append(className + "_" + indexAttribute + "_idx").append(";");
		execute(builder.toString());
	}

	public void dropUser(User user) throws SQLException {
		System.out.println("Dropping user " + user.getName() + "... ");
		for (Class<? extends DatabaseEntity> cls : user.getPrivileges()
				.keySet()) {
			try {
				StringBuilder builder = new StringBuilder("revoke all on ");

				String className = cls.getCanonicalName().equals(
						DatabaseEntity.Any.class.getCanonicalName()) ? "all tables in schema public"
						: cls.getSimpleName().toLowerCase();

				System.out.println("\tRevoking privileges on " + className
						+ "...");

				builder.append(className).append(" from ")
						.append(user.getName()).append(";");

				execute(builder.toString());
			} catch (Exception e) {
				System.out
						.println("\tWarning: Error while revoking user privileges!");
			}

			if (cls.getCanonicalName().equals(
					DatabaseEntity.Any.class.getCanonicalName())) {
				break;
			}
		}

		execute("drop user if exists " + user.getName() + ";");
	}

	public void createUser(User user) throws SQLException {
		StringBuilder builder = new StringBuilder("create user ")
				.append(user.getName()).append(" with password '")
				.append(user.getPassword()).append("';");
		execute(builder.toString());

		String sequencesPrivilege = "grant usage on all sequences in schema public to "
				+ user.getName();
		execute(sequencesPrivilege);

		System.out.print("Creating user " + user.getName()
				+ " with privileges: ");

		for (Class<? extends DatabaseEntity> cls : user.getPrivileges()
				.keySet()) {
			List<UserPrivilege> privileges = user.getPrivileges().get(cls);

			String className = cls.getCanonicalName().equals(
					DatabaseEntity.Any.class.getCanonicalName()) ? "all tables in schema public"
					: cls.getSimpleName().toLowerCase();

			builder = new StringBuilder("grant ");

			for (int idx = 0; idx < privileges.size();) {
				builder.append(privileges.get(idx).name().toLowerCase());
				System.out
						.print(privileges.get(idx).name().toLowerCase() + " ");
				if (++idx < privileges.size()) {
					builder.append(", ");
				}
			}
			System.out.print("(" + className + ") ");
			builder.append(" on ").append(className).append(" to ")
					.append(user.getName()).append(";");
			execute(builder.toString());
		}
		System.out.println();
	}

	public void insertEntities(Class<? extends DatabaseEntity> cls,
			DatabaseEntity... entities) throws SQLException {
		if (entities == null || entities.length == 0) {
			return;
		}

		String className = cls.getSimpleName().toLowerCase();

		StringBuilder builder = new StringBuilder("insert into ").append(
				className).append(" values ");

		for (Table table : tables) {
			if (table.getName().equals(className)) {

				for (int entityIdx = 0; entityIdx < entities.length;) {
					DatabaseEntity entity = entities[entityIdx];

					if (!entity.getClass().getSimpleName().toLowerCase()
							.equals(className)) {
						throw new RuntimeException(
								"Entity type doesn't match given class!");
					}

					builder.append(entity.toSqlValues());

					if (++entityIdx < entities.length) {
						builder.append(", ");
					}
				}

				builder.append(";");
				execute(builder.toString());
				return;
			}
		}
		throw new RuntimeException("No registered Entity type found!");
	}

	@Deprecated
	public final void executeRaw(String sqlString) throws SQLException {
		// TODO: Additional security handling to prevent malformed sql queries
		execute(sqlString);
	}

	public void dropAllMaterializedViews() throws SQLException {
		Statement statement = connection.createStatement();
		System.out.println("Dropping all existing materialized views...");
		ResultSet result = statement
				.executeQuery("SELECT 'DROP MATERIALIZED VIEW ' || string_agg(oid::regclass::text, ', ') FROM pg_class WHERE relkind = 'm';");
		if (result != null && result.next()) {
			String dropStmnt = result.getString(result.getMetaData()
					.getColumnCount());
			if (dropStmnt != null) {
				execute(new StringBuilder(dropStmnt).append(" cascade;")
						.toString());
			}
		}
		statement.close();
	}

	public void close() throws SQLException {
		connection.close();
	}
}
