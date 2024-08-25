package com.lessons.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Service
public class DatabaseService {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

	@Resource
	private DataSource dataSource;

	@Value("${app.datasource.database-name}")
	private String databaseName;


	private Map<String, String> mapTableNameToCsvOfColumns;    // The table name and column names are all in LOWER CASE within this map


	@PostConstruct
	public void init() {
		// Initialize the map of key=table-name and value=csv-of-column-names
		this.mapTableNameToCsvOfColumns = generateMapOfTableNameAndCsvOfColumnNames();
	}


	/**
	 * Query the database to get a map of key=table-name and value=csv-of-column-names
	 *
	 * @return map used by the AuditManager to get the csv of columns
	 */
	private Map<String, String> generateMapOfTableNameAndCsvOfColumnNames() {

		// Construct the SQL to get all columns for this **database name**
		String sql = "select table_name, string_agg(column_name::text, ',') as csv_columns\n" +
				"from information_schema.columns\n" +
				"where table_name not like 'pg_%' AND table_catalog=?  \n" +
				"group by table_name\n" +
				"order by 1 ";

		JdbcTemplate jt = new JdbcTemplate(this.dataSource);

		// Execute the SQL and generate a read-only record set
		SqlRowSet rs = jt.queryForRowSet(sql, this.databaseName);

		Map<String, String> mapOfInfo = new HashMap<>();

		// Loop through the results, adding information to the map
		while (rs.next()) {
			String tableName = rs.getString("table_name").toLowerCase();
			String csvColumns = rs.getString("csv_columns").toLowerCase();

			// Add this key/value pair to the map
			mapOfInfo.put(tableName, csvColumns);
		}

		logger.debug("The table cache holds {} records.", mapOfInfo.size());
		if (mapOfInfo.size() == 0) {
			throw new RuntimeException("Critical error in generateMapOfTableNameAndCsvOfColumnNames():  The cache holds 0 records.  This should never happen");
		}

		return mapOfInfo;
	}

	/**
	 * @return a unique number from the sequence
	 */
	public Integer getNextTableId() {
		String sql = "select nextval('seq_table_ids')";
		JdbcTemplate jt = new JdbcTemplate(this.dataSource);
		Integer newId = jt.queryForObject(sql, Integer.class);
		return newId;
	}

	public Map<String, String> getMapTableNameToCsvOfColumns() {
		return mapTableNameToCsvOfColumns;
	}

	/**
	 * @return unique number used for the id column on db tables
	 */
	public Integer getNextId() {
		String sql = "select nextval('seq_table_ids')";
		JdbcTemplate jt = new JdbcTemplate(this.dataSource);
		Integer nextId = jt.queryForObject(sql, Integer.class);
		return nextId;
	}

	public Integer getStartingVersionValue() {
		return 1;
	}

}