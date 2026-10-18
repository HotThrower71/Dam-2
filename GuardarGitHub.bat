@echo off
rem Ir a la carpeta donde esta este .bat (la raiz del repo)
cd /d "%~dp0"

echo ==============================
echo   Guardar cambios en GitHub
echo ==============================
echo.

git add .

rem Comprobar si hay algo que guardar
git diff --cached --quiet
if %errorlevel%==0 (
    echo No hay cambios nuevos que guardar.
    echo.
    pause
    exit /b
)

echo Cambios a guardar:
git status --short
echo.

set /p MSG=Mensaje del commit (Enter para usar fecha y hora): 
if "%MSG%"=="" set MSG=Guardado %date% %time:~0,5%

git commit -m "%MSG%"
if errorlevel 1 (
    echo.
    echo ERROR al hacer el commit.
    pause
    exit /b 1
)

git push
if errorlevel 1 (
    echo.
    echo ERROR al subir a GitHub. Revisa tu conexion o tus credenciales.
    pause
    exit /b 1
)

echo.
echo Listo: cambios guardados y subidos a GitHub.
pause
