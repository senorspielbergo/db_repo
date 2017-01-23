/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaphira.wahlinfo_setup.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author theralph
 */
public class SqlRunner {
    private static final Logger logger = Logger.getLogger(SqlRunner.class.getSimpleName());

	private static final String DEFAULT_DELIMITER = ";";	
	private static final Pattern NEW_DELIMITER_PATTERN = Pattern.compile("(?:--|\\/\\/|\\#)?!DELIMITER=(.+)");
	private static final Pattern COMMENT_PATTERN = Pattern.compile("^(?:--|\\/\\/|\\#).+");

        private static SqlRunner instance = new SqlRunner();
        
        private SqlRunner(){
            
        }
        public static SqlRunner instance(){
            return instance;
        }
        
        public static void runScript(Connection connection, InputStream scriptInputStream) throws SQLException, IOException {
		try (BufferedReader scriptReader = new BufferedReader(new InputStreamReader(scriptInputStream))) {
			StringBuffer command = null;
			String delimiter = DEFAULT_DELIMITER;
			String line = null;
			
			while ((line = scriptReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuffer();
				}
				
				String trimmedLine = line.trim();
	
				Matcher delimiterMatcher = NEW_DELIMITER_PATTERN.matcher(trimmedLine);
				Matcher commentMatcher = COMMENT_PATTERN.matcher(trimmedLine);
				
				// a) Delimiter change
				if (delimiterMatcher.find()) {
					delimiter = delimiterMatcher.group(1);					
					logger.log(Level.INFO, "SQL (new delimiter): " + delimiter);
				}
				
				// b) Comment
				else if (commentMatcher.find()) {
					logger.log(Level.INFO, "SQL (comment): " + trimmedLine);
				}
				
				// c) Statement
				else {
					command.append(trimmedLine);
					command.append(" ");
	
					// End of statement
					if (trimmedLine.endsWith(delimiter)) {
						logger.log(Level.INFO, "SQL: " + command);
	
						Statement statement = connection.createStatement();
						
						statement.execute(command.toString());
						statement.close();
						
						command = null;
					}
				}
			}
		}
	}
}
