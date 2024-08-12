# git reset --hard 0b575f355ef8b8c742b0f392650ffae278da8c9d
# kep-kernel-api first manual compile

analyzerList="
"

result=""
moduleName=""
modulePath=""
count=0

function fix_compile_errors(){

    if [ "$moduleName" = "" ]; then #1
      return
    fi
}

for analyzerData in $analyzerList; do
  if [ $((count % 2)) -eq 0 ]; then
    moduleName="$analyzerData"
  else
    modulePath="$analyzerData"
    
    cd $modulePath

    errorsBeforeAutomation=$(blade gw clean build 2>&1 | grep -iE '\b[0-9]+ error?s?\b' | awk '{print $1}')
    if [ -z "$errorsBeforeAutomation" ]; then
        errorsBeforeAutomation=0
    fi

    blade gw formatSource -Pjava.parser.enabled=false -Psource.check.category.names=Upgrade --continue

    git add . && git commit -m "Run SF in $moduleName" -a

    errorsAfterAutomation=$(blade gw clean build 2>&1 | grep -iE '\b[0-9]+ error?s?\b' | awk '{print $1}')
    if [ -z "$errorsAfterAutomation" ]; then
        errorsAfterAutomation=0
    fi
    
    log="$moduleName: $errorsBeforeAutomation"
    echo "$log"$'\n'

    result+="$log"$'\n'

    fix_compile_errors
    
    errorsAfterFixes=$(blade gw clean build 2>&1 | grep -iE '\b[0-9]+ error?s?\b' | awk '{print $1}')
    if [ -z "$errorsAfterFixes" ]; then
        errorsAfterFixes=0
    fi

    echo "Errors after fixes: $errorsAfterFixes"$'\n' 
  fi
  ((count++))
done

echo "Results:"
echo "$result"
