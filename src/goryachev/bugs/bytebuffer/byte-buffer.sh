#!/bin/sh

rm TestByteBuffer.class
/Library/Java/JavaVirtualMachines/jdk1.8.0_331.jdk/Contents/Home/bin/java -version
java -version
javac TestByteBuffer.java
mv TestByteBuffer.class TestByteBuffer-18.class

/Library/Java/JavaVirtualMachines/jdk1.8.0_331.jdk/Contents/Home/bin/javac TestByteBuffer.java

cp TestByteBuffer.class ../../../test.data/goryachev/bugs
java -cp ../../../test.data goryachev.bugs.TestByteBuffer
/Library/Java/JavaVirtualMachines/jdk1.8.0_331.jdk/Contents/Home/bin/java  -cp ../../../test.data goryachev.bugs.TestByteBuffer