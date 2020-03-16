# jarnalyze
Jarnalyze is an analysis tool for detecting Java classpath conflicts in .ear and .war files

Jarnalyze works on the final artifact (.ear and .war) which provides a different perspective than say mvn dependency:tree. 

Jarnalyze will look for conflicting resources (e.g., .class files) in the following places: 

* In WEB-INF/classes in .war files
* In WEB-INF/lib/*.jar in .war files
* In library jars in parent .ear files (designated by the Class-Path attribute of META-INF/MANIFEST.MF in .war files)

NOTE: Java 13 or newer required!

 
