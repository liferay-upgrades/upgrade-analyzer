# git reset --hard 0b575f355ef8b8c742b0f392650ffae278da8c9d
# kep-kernel-api first manual compile

analyzerList=" tudelft-employee-api /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/tudelft-employee/tudelft-employee-api
webservice-core /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/webservice-core
ssp-forms-kb-importer /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/ssp-forms-kb/ssp-forms-kb-importer
outdated-content-reporter /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/outdated-content-reporter
employee-portal-language /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/employee-portal-language
set-content-expiration /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/set-content-expiration
talentlink-portlet /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/talentlink-portlet
ckeditor-contributor /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/ckeditor-contributor
scroll-disabler /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/scroll-disabler
comment-portlet /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/comment-portlet
regular-employee-contrib /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/regular-employee-contrib
dynamic-nav-portlet /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/dynamic-nav-portlet
healthcheck-service /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/healthcheck-service
welcome-user-control-menu-portlet /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/welcome-user-control-menu-portlet
admin-theme-contributor /home/me/dev/projects/upgrades/lfrupg-tudelft/modules/admin-theme-contributor
"

result=""
moduleName=""
modulePath=""
count=0

function fix_compile_errors(){

    if [ "$moduleName" = "tudelft-employee-api" ]; then #1
      return
    fi
    if [ "$moduleName" = "webservice-core" ]; then #2
      return
    fi
    if [ "$moduleName" = "ssp-forms-kb-importer" ]; then #3
      return
    fi
    if [ "$moduleName" = "outdated-content-reporter" ]; then #4
      return
    fi
    if [ "$moduleName" = "employee-portal-language" ]; then #5
      return
    fi
    if [ "$moduleName" = "set-content-expiration" ]; then #6
      return
    fi
    if [ "$moduleName" = "talentlink-portlet" ]; then #7
      return
    fi
    if [ "$moduleName" = "ckeditor-contributor" ]; then #8
      return
    fi
    if [ "$moduleName" = "scroll-disabler" ]; then #9
      return
    fi
    if [ "$moduleName" = "comment-portlet" ]; then #10
      return
    fi
    if [ "$moduleName" = "regular-employee-contrib" ]; then #11
      return
    fi
    if [ "$moduleName" = "dynamic-nav-portlet" ]; then #12
      return
    fi
    if [ "$moduleName" = "healthcheck-service" ]; then #13
      return
    fi
    if [ "$moduleName" = "welcome-user-control-menu-portlet" ]; then #14
      return
    fi
    if [ "$moduleName" = "admin-theme-contributor" ]; then #15
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