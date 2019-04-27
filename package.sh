@set JAVA_HOME=%JAVA_HOME_1_6%
@set path=%JAVA_HOME_1_6%\bin;%path%
rmdir /s /q output
rmdir /s /q target
mvn clean install -Pprod