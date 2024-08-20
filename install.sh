filePath="dev/upgrades"
fileName="analyzer.jar"

function getLatestSnapshot {
    owner="liferay-upgrades"
    repository="upgrade-analyzer"
    tag="v2.0.1"
    snapshot="upgrade-analyzer-1.0-SNAPSHOT.jar"
    
    #create directory if not exists
    cd ~ && mkdir -p $filePath && cd $filePath && 
    curl -L \
    -o $fileName \
    https://github.com/$owner/$repository/releases/download/$tag/$snapshot
}

#$1=functionDeclaration
#$2=functionBody
function writeFunction {
    echo "function $1 {"$'\n'"    $2"$'\n}'
}

function addAliasOnBashrcFile {
    analyzerFunctionName="analyze_upgrade_project"

    #only add alias and function if analyze_upgrade_project function is not present on bashrc file
    if ! (grep -q $analyzerFunctionName ~/.bashrc); then  
        analyzerFunctionBody="java -jar ~/$filePath/$fileName \"\$@\""
        analyzerFunction=$(writeFunction "$analyzerFunctionName" "$analyzerFunctionBody")
        
        aliasFunctionBody="alias aup=\""$analyzerFunctionName"\""
        aliasFunctionName=upgrade_analyzer_alias
        aliasFunction=$(writeFunction "$aliasFunctionName" "$aliasFunctionBody")

        echo $'\n\n'"$analyzerFunction"$'\n\n'"$aliasFunction"$'\n\n'"$aliasFunctionName">> ~/.bashrc 
    fi
}

getLatestSnapshot
addAliasOnBashrcFile