cd simulation
call mvn compile
cls
call mvn exec:java -Dexec.mainClass=manager.Main
pause