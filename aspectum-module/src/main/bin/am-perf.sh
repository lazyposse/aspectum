#!/bin/bash -e

ASPECTUM_VERSION="0.0.3"
CONFIG_DIR="$HOME/.am-perf"
CONFIG_FILE="config.txt"
CONFIG_PATH="$CONFIG_DIR"/"$CONFIG_FILE"

function ask {
    while true; do

        if [ "${2:-}" = "Y" ]; then
            prompt="Y/n"
            default=Y
        elif [ "${2:-}" = "N" ]; then
            prompt="y/N"
            default=N
        else
            prompt="y/n"
            default=
        fi

        # Ask the question
        read -p "$1 [$prompt] " REPLY

        # Default?
        if [ -z "$REPLY" ]; then
            REPLY=$default
        fi

        # Check if the reply is valid
        case "$REPLY" in
            Y*|y*) return 0 ;;
            N*|n*) return 1 ;;
        esac

    done
}

USE_CONFIG="n"
if [ -f "$CONFIG_PATH" ];
then
    source "$CONFIG_PATH"
    echo
    echo "------------------ Use config from previous session? ---------------------------"
    echo
    echo "In $CONFIG_PATH, there is:"
    echo "    - Home directory="\'"$APP_HOME"\'
    echo "    - Java command  ="\'"$APP_CMD"\'
    echo
    ask "> Use it?" Y && USE_CONFIG="y"
fi

echo
if [ "$USE_CONFIG" == "n" ];
then
    echo
    echo "----------------------------- Configuring --------------------------------------"
    echo
    echo -n "> Directory where the java command line is launched?: "
    read APP_HOME
    echo -n "> Command line to launch the app? (java -cp ...)    : "
    read APP_CMD
    echo
    echo
    echo "------------------ Save config for futures sessions? ---------------------------"
    echo

    if ask "> Will be saved to $CONFIG_PATH. Proceed?" Y; then
        mkdir -p "$CONFIG_DIR"
        echo "APP_HOME=\"$APP_HOME\"" > "$CONFIG_PATH"
        echo "APP_CMD=\"$APP_CMD\""  >> "$CONFIG_PATH"
    fi
fi

echo
echo
echo "--------------------------- Testing the cmd ------------------------------------"
echo
echo "  - Your app should work with:"
echo "cd $APP_HOME && $APP_CMD"
echo

APP_CMD_RET=
if ask "> Run the app to test your conf? (recommended if not done before)" Y; then
    set -x
    cd "$APP_HOME"
    set +e
    eval $APP_CMD
    APP_CMD_RET=$?
    set -e
    set +x
fi

if [[ $APP_CMD_RET ]];
then
    echo
    echo
    echo "------------------------------ Config OK? --------------------------------------"
    echo
    if ! ask "> App returned code $APP_CMD_RET, continue?" Y; then
        echo -e "\nbye!\n"
        exit 1
    fi
fi
echo
echo
echo

JAR_REXP='\s-jar\s'
if echo "$APP_CMD" | grep "$JAR_REXP" > /dev/null;
then
    echo "--------------------------- Checking the cmd -----------------------------------"
    echo
    echo "Problem found:"
    echo
    echo "    * -jar option found in cmd line: "
    echo
    echo "$APP_CMD" | grep --color "$JAR_REXP"
    echo
    echo "    * It will not work with a javaagent!"
    echo
    ask "> Continue anyway?" N || echo -e "\nBye!\n" ; exit 1
    echo
    echo
    echo
fi

AHOME="$(readlink -f "$(dirname "$0")/../../../..")"

#mvn install dependency:copy-dependencies

CP_TO_ADD=
CP_TO_ADD+="$AHOME"/aspectum-module/target/test-classes:
CP_TO_ADD+="$AHOME"/aspectum-core/target/aspectum-core-$ASPECTUM_VERSION.jar:
CP_TO_ADD+="$AHOME"/aspectum-module/target/aspectum-module-$ASPECTUM_VERSION.jar:
CP_TO_ADD+="$AHOME"/aspectum-api/target/aspectum-api-$ASPECTUM_VERSION.jar

AJ_TO_ADD="-javaagent:""$AHOME"/aspectum-module/target/dependency/aspectjweaver-1.7.0.jar

PROP_TO_ADD="-Daspectum.callback=aspectum.module.MethodTimeCallback"

echo "---------------------- Building NEW command line -------------------------------"
echo

NEW_CMD=
AGENT_DONE=
CP_DONE=
PROP_DONE=
for I in $APP_CMD;
do
    if   [ -z $AGENT_DONE ] && echo "$I" | grep '^java$'              > /dev/null; then NEW_CMD+="$I $AJ_TO_ADD "  ; AGENT_DONE="y"
    elif [ -z $CP_DONE    ] && echo "$I" | grep '^-cp$\|^-classpath$' > /dev/null; then NEW_CMD+="$I ""$CP_TO_ADD:" ; CP_DONE="y"
    elif [ -z $PROP_DONE  ] && echo "$I" | grep '^-D'                 > /dev/null; then NEW_CMD+="$PROP_TO_ADD $I " ; PROP_DONE="y"
    else                                                                                NEW_CMD+="$I "
    fi
done

echo
echo "    * New command line:"
echo "    -------------------"
echo
echo "        cd $APP_HOME && $NEW_CMD"
echo
echo "    * New command line (multiline):"
echo "    -------------------------------"
echo
echo "cd $APP_HOME && \\"
IS_FIRST="y"
for I in $NEW_CMD;
do
    if [[ $IS_FIRST ]]; then
        echo -n "$I"
        IS_FIRST=
    else
        echo -ne " \\"
        echo
        echo -n "$I"
    fi
done
echo
echo

exit 1

java -javaagent:"$AGENT"                                    \
    -cp "$CP"                                               \
    -Daspectum.callback=aspectum.module.MethodTimeCallback  \
    aspectum.sampleapp.SampleApplication
