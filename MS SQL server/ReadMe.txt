1. import jar file
2. copy sqljdbc_auth.dll (depending on the processor) and put in either jre or jdk folder (both in C:\Program Files\java)
3. server url string should be jdbc:sqlserver://[instance_name];databaseName=[];integratedSecurity=true

** integratedSecurity=true means it's using WindowsAuthentication