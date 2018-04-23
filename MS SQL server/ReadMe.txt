1. import mssql-jdbc-6.2.2.jre7.jar or mssql-jdbc-6.2.2.jre8.jar file depending on the java version on the machine
2. copy sqljdbc_auth.dll in auth folder (depending on the processor) and put in either jre or jdk folder (both in C:\Program Files\java)
3. server url string should be jdbc:sqlserver://[instance_name];databaseName=[];integratedSecurity=true

** integratedSecurity=true means it's using WindowsAuthentication