JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Receiver.java \
	Sender.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
