## Java-DB
**Description: Performs DB operations on SQL server or Oracle DB**
Depending on the type of connections you need (SQL Server or Oracle) call either 
`DBActivity sql = new SQLActivity(dburl, username, password);` or `DBActivity oracle = new OracleActivity(dburl, username, password);`

The methods are the same for each instance. Will use sql as an example for the following code.

**Usage:**
<u>To insert single record:</u>
`int[] result = sql.insert(String tableName, String[] keys, Object[] values);`
> tableName = the table name in the DB
> Keys = the column you want to insert into
> Values = the new values associated with the column
> 
<u>To insert multiple records: </u>
`int[] results = sql.insert(String tableName, String[] keys, Object[][] values);`
> Values = 2D array to take more than 1 pair values to insert
>
<u>To update single record:</u>
`int result = sql.update(String tableName, String [] keys, Object[] values, String[][] condition, String intermediate);`
> tableName = the table name in the DB
> Keys = the column you want to update
> Values = the new value associated with the column
> Condition = the "WHERE CLAUSE".
> Intermediate = the conjunction when you have more than  1 condition
> for example: 
> SQL: update ...  where ID = 50 AND ID_2 = 100
> you would put condition = new String[][]{{"ID", "50"}, {"ID_2", "100"}} 
> intermediate = "AND"
> 
<u>To update multiple records:</u>
`int[] results = sql.update(String tableName, String[] keys, Object[][] values, String condition_field, String[] condition_vals)`
> Values = 2D array to take in more than 1 pair of values to update
> Condition_vals = an array to take in different condition values.
> 
<u>To delete single record:</u>
`int result = sql.delete(String tableName, String[] keys, Object[] values, String condition)`
> tableName = the table name in DB
> keys = condition column in the WHERE CLAUSE
> values = the value associated with the column
> condition = intermediate if you have more than 1 key
> for example: delete from table where A = 5 AND B = 6
> keys = new String[]{"A", "B"}
> values = new Object[]{"5", "6"}
> condition = "AND"
> 
<u>To delete multiple records:</u>
`int[] results = sql.delete(String tableName, String[] keys, Object[][] values, String condition)`
> values = 2D array to take in more than 1 pair of values
> 
<u>To select:</u>
`ResultSet r = sql.select(String tableName, String[] choices, Object[][] conditions, String logic)`
> tableName = table name in DB
> choices = the columns you want to select
> conditions = WHERE clause
> logic = "AND" or "OR"
> for example: select A from table where id=5 AND id_2 = 6
> choices = new String[]{"A"}
> conditions = new Object[][]{{"ID", "5"}, {"ID_2","6"}}
> logic = "AND"
>  
OR
`ResultSet r = sql.executeQuery(String sql);`
> sql is the complete SQL query
> 
<u>To getRowCount:</u>
`int totalRows = sql.getRowCount(String statement);`
> statement is the complete SQL.
> for example: select count(*) from table ...
> 
<u>To close connection object</u>
`sql.close()`
