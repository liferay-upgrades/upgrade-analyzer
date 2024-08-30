install_file_path=".liferay-upgrades-analyzer"
file_name="upgrade-analyzer.jar"

function getLatestSnapshot {
    location="$(curl -I -s https://github.com/liferay-upgrades/upgrade-analyzer/releases/latest | grep "location:" | cut -d " " -f 2 | sed 's/.$//')"
    url="${location}/${file_name}"

    echo "Starting download from ${url}"
    
    #create directory if not exists
    cd ~ && mkdir -p $install_file_path && cd $install_file_path &&
    curl -L \
    -o $file_name \
    $url
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
        analyzerFunctionBody="java -jar ~/$install_file_path/$file_name \"\$@\""
        analyzerFunction=$(writeFunction "$analyzerFunctionName" "$analyzerFunctionBody")
        
        aliasFunctionBody="alias aup=\""$analyzerFunctionName"\""
        aliasFunctionName=upgrade_analyzer_alias
        aliasFunction=$(writeFunction "$aliasFunctionName" "$aliasFunctionBody")

        echo $'\n\n'"$analyzerFunction"$'\n\n'"$aliasFunction"$'\n\n'"$aliasFunctionName">> ~/.bashrc 
    fi
}

getLatestSnapshot
#addAliasOnBashrcFile