JC = javac
BUILD_DIR = test
JFLAGS = -g -d $(BUILD_DIR)
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Kernelizr.java \
#        Foo.java \
		Bar.java \
		Baz.java

# help message by default
.PHONY: default
default:
		@echo make build   - build project.
		@echo make clean   - remove generated classes and jars.
		@echo make rebuild - clean and build project.
		@echo make run     - run
		@echo make jar     - rebuild and package into a jar (BROKEN!).

classes: src/$(CLASSES:.java=.class)

build: classes

clean:
		$(RM) $(BUILD_DIR)/kernelizr/*.class
		$(RM) ./*.jar

rebuild: clean build

run:
		java -cp test/ kernelizr.Kernelizr

jar: rebuild
		jar cvfm kernelizr.jar src/META-INF/MANIFEST.mf $(BUILD_DIR)/kernelizr/$(CLASSES:.java=.class)
