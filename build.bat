cd maven_plugins
call mvn install -DskipTests
cd ..
cd base_library
call mvn install -DskipTests
cd ..
cd maritime_library
call mvn install -DskipTests
cd ..
call mvn package -DskipTests
pause