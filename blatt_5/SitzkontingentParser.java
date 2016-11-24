import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SitzkontingentParser {

	public static void main(String[] args) {
		if (args.length != 1) {
			return;
		}
		File file = new File(args[0].concat(File.separator
				+ "sitzkontingente.csv"));
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			SqlRunner.instance()
					.execute("DROP TABLE IF EXISTS sitzkontingent;");
			SqlRunner
					.instance()
					.execute(
							"CREATE TABLE sitzkontingent ("
									+ "id int primary key,"
									+ " bundesland varchar(30) references bundesland(name),"
									+ " int wahljahr not null,"
									+ " kontingent int not null" + ");");
			List<String[]> tuples = new ArrayList<String[]>();
			lines.stream()
					.forEach(
							l -> {
								String[] v = l.split(",");
								tuples.add(new String[] {
										String.valueOf(tuples.size()), v[0],
										v[1], v[2] });
							});
			SqlRunner.instance().execute(
					getSqlStatement("sitzkontingent", tuples, new String[] {
							"int", "varchar", "int", "int" }));
			SqlRunner.instance().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSqlStatement(String table, List<String[]> values,
			String[] types) throws OutOfMemoryError {
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(table);
		builder.append(" values ");
		for (int pidx = 0; pidx < values.size(); pidx++) {
			builder.append("(");
			String[] s = values.get(pidx);
			for (int sidx = 0; sidx < s.length - 1; sidx++) {
				if (!s[sidx].contains("#NULL#")
						&& types[sidx].equals("varchar")) {
					builder.append("'");
					builder.append(s[sidx]);
					builder.append("', ");
				} else {
					String v = s[sidx];
					if (s[sidx].contains("#NULL#")) {
						v = "NULL";
					}
					builder.append(v);
					builder.append(", ");
				}
			}
			if (!s[s.length - 1].contains("#NULL#")
					&& types[s.length - 1].equals("varchar")) {
				builder.append("'");
				builder.append(s[s.length - 1]);
				builder.append("')");
			} else {
				String v = s[s.length - 1];
				if (s[s.length - 1].contains("#NULL#")) {
					v = "NULL";
				}
				builder.append(v);
				builder.append(")");
			}
			if (pidx < values.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(";");
		return builder.toString();
	}
}
