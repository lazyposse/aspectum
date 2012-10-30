#!/bin/bash -xe

NAME="$(basename $0)"
HOME="$(readlink -f "$(dirname "$0")/..")"
cd "$HOME"

NEW_VERSION=$1

if [ -z $NEW_VERSION ] ;
then
    set +x
    echo
    echo "+-----------------------------------------------------------------------------+"
    echo "|                                                                              "
    echo "| Usage      :                                                                 "
    echo "|     $NAME <new-version>                                                      "
    echo "|                                                                              "
    echo "| For example:                                                                 "
    echo "|     $NAME 1.2.3                                                              "
    echo "|                                                                              "
    echo "+-----------------------------------------------------------------------------+"
    echo
    exit 1
fi

PREV_VERSION=$(grep '<version>.*</version>' pom.xml | sed 's/\s*<version>\(.*\)<\/version>\s*/\1/g')

set +x
echo
echo "    Old version is     : $PREV_VERSION"
echo "    Setting new version: $NEW_VERSION"
echo
set -x

find . -name 'pom.xml' -exec sed 's/'"$PREV_VERSION"'/'"$NEW_VERSION"'/g' -i {} \;
