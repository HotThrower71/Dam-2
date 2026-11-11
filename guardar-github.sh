#!/usr/bin/env bash
# Guarda los cambios de la carpeta en GitHub (add + commit + push)
cd "$(dirname "$(readlink -f "$0")")" || exit 1

echo "=============================="
echo "  Guardar cambios en GitHub"
echo "=============================="
echo

git add -A

# Comprobar si hay algo que guardar
if git diff --cached --quiet; then
    echo "No hay cambios nuevos que guardar."
    read -rp "Pulsa Enter para salir..." _
    exit 0
fi

echo "Cambios a guardar:"
git status --short
echo

read -rp "Mensaje del commit (Enter para usar fecha y hora): " MSG
[ -z "$MSG" ] && MSG="Guardado $(date '+%d/%m/%Y %H:%M')"

if ! git commit -m "$MSG"; then
    echo
    echo "ERROR al hacer el commit."
    read -rp "Pulsa Enter para salir..." _
    exit 1
fi

if ! git push; then
    echo
    echo "ERROR al subir a GitHub. Revisa tu conexion o tu clave SSH."
    read -rp "Pulsa Enter para salir..." _
    exit 1
fi

echo
echo "Listo: cambios guardados y subidos a GitHub."
read -rp "Pulsa Enter para salir..." _
