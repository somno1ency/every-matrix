# build all by default, even if it's not first
.DEFAULT_GOAL := all

PROJECT_NAME := stake.jar
JAVAC := javac
JAR := jar
JAVA := java

.PHONY: all
all: build package run

# need to know the dependency of every class
.PHONY: build
build:
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/shared/Constant.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/cache/BidirectionalCache.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/dto/ApiResult.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/dto/SessionResp.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/model/StakeInfo.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/pool/NamingThreadFactory.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/pool/ThreadPoolInitializer.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/util/ResponseUtil.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/util/StringUtil.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/handler/DispatcherHandler.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/StakeApplication.java

.PHONY: package
package:
	@$(JAR) cvfm $(PROJECT_NAME) MANIFEST.MF -C out/ .

.PHONY: run
run:
	@$(JAVA) -jar $(PROJECT_NAME)