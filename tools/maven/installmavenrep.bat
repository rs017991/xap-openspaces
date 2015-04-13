@echo off

if "%1" == ""  (
@rem The call to setenv.bat can be commented out if necessary.
@call "%~dp0\..\..\bin\setenv.bat"
)

if "%1" == "edition"  (
%JAVACMD% -cp %GS_JARS% org.openspaces.maven.support.OutputVersion edition
goto END
)

if "%1" == "XAP"  (
%JAVACMD% -cp %GS_JARS% org.openspaces.maven.support.OutputVersion XAP
goto END
)

FOR /F "usebackq tokens=*" %%i IN (`installmavenrep.bat edition`) DO @set EDITION=%%i

FOR /F "usebackq tokens=*" %%i IN (`installmavenrep.bat XAP`) DO @set XAP_VERSION=%%i

echo ""
echo ""
echo "Installing XAP %XAP_VERSION% jars"
echo ""
echo ""

REM GigaSpaces Jars
call mvn install:install-file -DgroupId=com.gigaspaces -DcreateChecksum=true -DartifactId=gs-runtime -Dversion=%XAP_VERSION% -DpomFile=%JSHOMEDIR%/tools/maven/poms/gs-runtime/pom.xml -Dpackaging=jar -Dfile="%JSHOMEDIR%/lib/required/gs-runtime.jar" -Djavadoc="%JSHOMEDIR%/docs/xap-javadoc.zip"
call mvn install:install-file -DgroupId=com.gigaspaces -DcreateChecksum=true -DartifactId=gs-openspaces -Dversion=%XAP_VERSION% -DpomFile=%JSHOMEDIR%/tools/maven/poms/gs-openspaces/pom.xml -Dpackaging=jar -Dfile="%JSHOMEDIR%/lib/required/gs-openspaces.jar" -Djavadoc="%JSHOMEDIR%/docs/xap-javadoc.zip" -Dsources="%JSHOMEDIR%/lib/optional/openspaces/gs-openspaces-sources.jar"
call mvn install:install-file -DgroupId=com.gigaspaces -DcreateChecksum=true -DartifactId=mule-os -Dversion=%XAP_VERSION% -Dpackaging=jar -DpomFile=%JSHOMEDIR%/tools/maven/poms/mule-os/pom.xml -Dfile="%JSHOMEDIR%/lib/optional/openspaces/mule-os.jar"
call mvn install:install-file -DgroupId=com.gigaspaces -DcreateChecksum=true -DartifactId=jetty-os -Dversion=%XAP_VERSION% -Dpackaging=jar -DpomFile=%JSHOMEDIR%/tools/maven/poms/jetty-os/pom.xml -Dfile="%JSHOMEDIR%/lib/platform/openspaces/gs-openspaces-jetty.jar"
call mvn install:install-file -DgroupId=com.gigaspaces -DcreateChecksum=true -DartifactId=mongo-datasource -Dversion=%XAP_VERSION% -Dpackaging=jar -DpomFile=%JSHOMEDIR%/tools/maven/poms/mongo-datasource/pom.xml -Dfile="%JSHOMEDIR%/lib/optional/datasource/mongo/mongo-datasource.jar"

REM Build and install OpenSpaces Maven Plugin 
call mvn -f maven-openspaces-plugin/pom.xml install  -DcreateChecksum=true

:END
