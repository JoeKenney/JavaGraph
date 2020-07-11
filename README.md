# Introduction

This is an example project to explore the possibility of tracking Java synchronization structures at runtime. This project uses the Java instrumentation classes (https://docs.oracle.com/en/java/javase/14/docs/api/java.instrument/java/lang/instrument/package-summary.html).



# Running the tests

Build the package and download dependencies

mvn package

This project includes some test cases that you can run.  To get the dependency classpath from maven run the following command and copy out the gigantic list of jar files in the middle of the output

mvn dependency:build-classpath

To run the test code:

java -cp <Classpath_from above>:target/JavaAgent-0.0.1-SNAPSHOT.jar -javaagent:target/JavaAgent-0.0.1-SNAPSHOT.jar org.javagraph.test.Test
