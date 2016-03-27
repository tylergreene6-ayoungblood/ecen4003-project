JC = javac
BUILD_DIR = ../test
JFLAGS = -g -d $(BUILD_DIR) -Xlint:all
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Kernelizr.java \
		KKernel.java \
		KTask.java \
		KOps.java

# help message by default
.PHONY: default
default:
		@echo "make build   - build project."
		@echo "make clean   - remove generated classes and jars."
		@echo "make rebuild - clean and build project."
		@echo "make run     - build and run."
		@echo "make jar     - rebuild and package into a jar (BROKEN!)."

classes: $(CLASSES:.java=.class)

build: classes

clean:
		$(RM) $(BUILD_DIR)/*.class
		$(RM) ../*.jar

rebuild: clean build

run: build
		java -cp $(BUILD_DIR)/ Kernelizr $(BUILD_DIR)/datasets/image/103-menger-3840x2160-2_cropped_640x640.png

jar: rebuild
		jar cvfm ../Kernelizr.jar META-INF/MANIFEST.mf $(BUILD_DIR)/$(CLASSES:.java=.class)
