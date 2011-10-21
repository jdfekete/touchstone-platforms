#javac -d . -cp lib/antlr.jar:lib/commons-lang-2.1.jar:lib/javaosc.jar:lib/jdom.jar:lib/jinput-linux.jar:lib/jinput.jar:lib/js.jar:lib/touchstone.jar:lib/SwingStates.jar @sourcefiles
#java -cp lib/antlr.jar:lib/commons-lang-2.1.jar:lib/javaosc.jar:lib/jdom.jar:lib/jinput-linux.jar:lib/jinput.jar:lib/js.jar:lib/touchstone.jar:lib/SwingStates.jar:. graphic.DesignPlatform

#!/bin/sh

echo
echo "TouchStone Build System"
echo "-------------------"
echo

if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

LOCALCLASSPATH="$JAVA_HOME"/lib/tools.jar:./tools/lib/ant.jar:./tools/lib/ant-launcher.jar
ANT_HOME=./tools

echo Building with classpath $CLASSPATH:$LOCALCLASSPATH
echo

echo Starting Ant...
echo

export ANT_OPTS="-Xms1024m -Xmx1024m"
#setenv ANT_OPTS "-Xms1024m -Xmx1024m"

"$JAVA_HOME"/bin/java -Dant.home="$ANT_HOME" -classpath "$LOCALCLASSPATH:$CLASSPATH" org.apache.tools.ant.Main $*
