1. Open java project -> import files (all in src folder)
2. Set vm arguments: "-Xms512m -Xmx2048m"
3. Set program arguments: "*your repo path*/blatt_4/Wahldaten" [-w] 
 (-w if you want to write directly to the database; otherwise, a sql file is created)
4. Make sure postgreSQL is installed, the postgreSQL server is listening at port 5432, a database named "wahlinfo_db" exists and can be accessed with
user "kaphira" and password "cowboyohnepony"
5. Run the code!


Troubleshooting: If a Garbage Collector OutOfMemoryError occurs, set the following arguments for your java vm:
"-XX:+UseG1GC
-XX:PermSize=512M
-XX:MaxPermSize=1024M"