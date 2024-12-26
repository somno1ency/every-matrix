# build all by default, even if it's not first
.DEFAULT_GOAL := all

PROJECT_NAME := stake.jar
JAVAC := javac
JAR := jar
JAVA := java
CP := cp

.PHONY: all
all: build package run

# need to know the dependency of every class
.PHONY: build
build:
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/shared/Constant.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/cache/BidirectionalCache.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/model/StakeInfo.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/strategy/RouterStrategy.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/util/StringUtil.java
	@$(JAVAC) -d out src/main/java/com/everymatrix/stake/util/TimeUtil.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/util/ResponseUtil.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/manager/SessionManager.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/manager/StakeManager.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/job/SessionCleaner.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/job/StakeCleaner.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/context/RouterContext.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/handler/DispatcherHandler.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/strategy/impl/GetSessionRouterStrategy.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/strategy/impl/GetHighStakesRouterStrategy.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/strategy/impl/PostStakeRouterStrategy.java
	@$(JAVAC) -d out -classpath out src/main/java/com/everymatrix/stake/StakeApplication.java
	@$(CP) -r src/main/resources/META-INF out/META-INF

.PHONY: package
package:
	@$(JAR) cvfm $(PROJECT_NAME) MANIFEST.MF -C out/ .

.PHONY: run
run:
	@$(JAVA) -jar $(PROJECT_NAME)