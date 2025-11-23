@echo off
echo ========================================
echo    MadMaze - Package de Distribution
echo ========================================
echo.

REM Créer le dossier de distribution
set "DIST_DIR=MadMaze-Distribution"
if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
mkdir "%DIST_DIR%"
mkdir "%DIST_DIR%\lib"

echo [1/4] Compilation du projet...
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo Erreur lors de la compilation!
    pause
    exit /b 1
)

echo [2/4] Copie des dépendances...
call mvn dependency:copy-dependencies -DoutputDirectory="%DIST_DIR%\lib" -DincludeScope=runtime
if %ERRORLEVEL% neq 0 (
    echo Erreur lors de la copie des dépendances!
    pause
    exit /b 1
)

echo [3/4] Copie du JAR principal...
copy "target\labyrinthe-1.0-SNAPSHOT.jar" "%DIST_DIR%\"

echo [4/4] Création du script de lancement...
(
echo @echo off
echo setlocal
echo.
echo REM Définir le répertoire du script
echo set "SCRIPT_DIR=%%~dp0"
echo.
echo REM Définir le classpath avec toutes les dépendances
echo set "CLASSPATH=%%SCRIPT_DIR%%labyrinthe-1.0-SNAPSHOT.jar"
echo for %%%%f in ^("%%SCRIPT_DIR%%lib\*.jar"^) do call set "CLASSPATH=%%%%CLASSPATH%%%%;%%%%f"
echo.
echo REM Lancer l'application JavaFX
echo java --module-path "%%SCRIPT_DIR%%lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "%%CLASSPATH%%" main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView
echo.
echo if %%%%ERRORLEVEL%%%% neq 0 ^(
echo     echo.
echo     echo Erreur lors du lancement de l'application!
echo     pause
echo ^)
) > "%DIST_DIR%\MadMaze.bat"

echo.
echo ========================================
echo Package créé avec succès dans: %DIST_DIR%
echo Pour lancer l'application, exécutez: %DIST_DIR%\MadMaze.bat
echo ========================================
pause
