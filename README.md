# jarnalyze
Jarnalyze is an analysis tool for detecting Java classpath conflicts in ear, war and spring-boot fat jar files

Jarnalyze works on the final artifact (.ear, .war or .jar) which provides a different perspective than tools like mvn dependency:tree. 

Jarnalyze will look for conflicting resources (e.g., .class files) in the following places: 

* WEB-INF/classes in war files
* WEB-INF/lib/*.jar in war files
* BOOT-INF/classes in spring-boot fat jar files
* BOOT-INF/lib/*.jar in spring-boot fat jar files
* In library jars in parent ear files (designated by the Class-Path attribute of META-INF/MANIFEST.MF in war files)

## Buildling jarnalyze

```console
mvn clean install
```

## Running jarnalyze

```console
./jarnalyze -help
```

**NOTE: Java 13 or newer is required!**

 
