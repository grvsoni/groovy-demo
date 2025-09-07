
def sendEmail() {
  if (config.sendAttachments && config.fileSet != null) {
    emailext subject: parseVars(config.emailSubject, replaces),
      attachLog: config.attachLog,
      compressLog: config.compressLog,
      to: config.emailTo,
      body: parseVars(config.emailBody, replaces),
      mimeType: 'text/html',
      attachmentsPattern: config.fileSet
  } else {
    emailext subject: parseVars(config.emailSubject, replaces),
      attachLog: config.attachLog,
      compressLog: config.compressLog,
      to: config.emailTo,
      body: parseVars(config.emailBody, replaces)
  }
}

def sendEmailTemplate() {
  def emailBody = readFile "${config.emailFile}"
  if (config.loadFileContents) {
    emailBody = loadFileContents(emailBody)
  }
  if (config.sendAttachments && config.fileSet != null) {
    emailext subject: parseVars(config.emailSubject, replaces),
      attachLog: config.attachLog,
      compressLog: config.compressLog,
      to: config.emailTo,
      body: parseVars(emailBody, replaces),
      mimeType: 'text/html',
      attachmentsPattern: config.fileSet
  } else {
    emailext subject: parseVars(config.emailSubject, replaces),
      attachLog: config.attachLog,
      compressLog: config.compressLog,
      to: config.emailTo,
      body: parseVars(emailBody, replaces),
      mimeType: 'text/html'
  }
}

def loadFileContents(emailBody) {
  def retBody = readFile "${config.emailFile}"
  if (retBody instanceof String) {
    while (getMatch (retBody, /\$\{FILE,path=(.*?)\}/) != false) {
      match = getMatch (retBody, /\$\{FILE,path=(.*?)\}/)
      fileContent = readFile "${match}"
      retBody = retBody.replaceAll (/\$\{FILE,path=(.*?)\}/, fileContent)
    }
  }
  return retBody
}

def pullGit() {
  if (config.customSubfolder != null) {
    println ">> Cloning ssh://git@${config.gitInstance}.transunion.com:7999/${config.project}/${config.repo}.git into custom subfolder: ${config.customSubfolder}..."
    checkout([
      $class: 'GitSCM',
      branches: [[name: config.branch]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[
	$class: 'RelativeTargetDirectory',
	relativeTargetDir: "${config.customSubfolder}"
      ]],
      submoduleCfg: [],
      userRemoteConfigs: [[
	credentialsId: config.credentialsId,
	url: "ssh://git@${config.gitInstance}.transunion.com:7999/${config.project}/${config.repo}.git"
      ]]
    ])	
  } else {
    println ">> Cloning ssh://git@${config.gitInstance}.transunion.com:7999/${config.project}/${config.repo}.git..."
    checkout([
      $class: 'GitSCM',
      branches: [[name: config.branch]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[
	$class: 'RelativeTargetDirectory',
	relativeTargetDir: "${config.repo}"
      ]],
      submoduleCfg: [],
      userRemoteConfigs: [[
	credentialsId: config.credentialsId,
	url: "ssh://git@${config.gitInstance}.transunion.com:7999/${config.project}/${config.repo}.git"
      ]]
    ])
  }
}

def pullAdo() {
  if (config.customSubfolder != null) {
    println ">> Cloning ${config.adoProject} from https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject} into custom subfolder ${config.customSubfolder}..."
    checkout([
      $class: 'TeamFoundationServerScm',
      credentialsConfigurer: [$class: 'AutomaticCredentialsConfigurer'],
      serverUrl: "https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}",
      projectPath: "${config.adoProject}",
      localPath: "${config.customSubfolder}",
      useOverwrite: true,
      useUpdate: true
    ])
  } else {	
    println ">> Cloning ${config.adoProject} from https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}..."
    checkout([
      $class: 'TeamFoundationServerScm',
      credentialsConfigurer: [$class: 'AutomaticCredentialsConfigurer'],
      serverUrl: "https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}",
      projectPath: "${config.adoProject}",
      useOverwrite: true,
      useUpdate: true
    ])
  }
}

def pullAdoGit() {
  if (config.customSubfolder != null) {
    println ">> Cloning https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}/_git/${config.adoRepo}..."
    checkout([
      $class: 'GitSCM',
      branches: [[name: config.branch]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[
	$class: 'RelativeTargetDirectory',
	relativeTargetDir: "${config.customSubfolder}"
      ]],
      submoduleCfg: [],
      userRemoteConfigs: [[
	credentialsId: config.credentialsId,
	url: "https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}/_git/${config.adoRepo}"
      ]]
    ])
  } else {	
    println ">> Cloning https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}/_git/${config.adoRepo}..."
    checkout([
      $class: 'GitSCM',
      branches: [[name: config.branch]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[
	$class: 'RelativeTargetDirectory',
	relativeTargetDir: "${config.adoRepo}"
      ]],
      submoduleCfg: [],
      userRemoteConfigs: [[
	credentialsId: config.credentialsId,
	url: "https://${config.adoInstance}.transunion.com/${config.adoCollection}/${config.adoTeamProject}/_git/${config.adoRepo}"
      ]]
    ])
  }
}

def runCommand(commandType, command) {
  timeout(activity: true, time: config.timeoutMinutes) {	
    if (commandType == 'raw-bat' || commandType == 'script-bat') {
      bat command
    } else if (commandType == 'script-sh') {
      sh 'sh ./' + command
    } else if (commandType == 'raw-sh') {
      sh command
    } else if (commandType == 'textdump-sh') {	    
      scriptBody = readFile "${config.command}"
      sh scriptBody
    } else if (commandType == 'script-python') {
      bat "python ${command} ${config.reportLocation} ${config.propsFile}"
    }
  }
}

def runPasswordedCommand(commandType, command) {
  withCredentials([string(credentialsId: config.concealedPasswordId, variable: 'concealedPassword')]) {
    timeout(activity: true, time: config.timeoutMinutes) {
      if (commandType == 'raw-bat' || commandType == 'script-bat') {
	bat command
      } else if (commandType == 'raw-sh' || commandType == 'script-sh') {
	sh './' + command
      } else if (commandType == 'script-python') {
	bat "python ${command} ${config.reportLocation} ${config.propsFile}"
      }
    }
  }
}

def nodeProcess () {
  //Set custom parameters as envars
  config.customparams.each{ key, val ->
    val = val in String ? val : val.toString()
    if (val[0] == '%' && val[-1] == '%') {
      env[key] = env[val[1..-2]]
      println ">> Exporting envar to existing value: ${key}:${val} = ${env[val[1..-2]]}"
    } else {
      env[key] = val
      println ">> Exporting envar: ${key} = ${val}"
    }
  }  

  //Pull the source repo    
  if (config.pullTestRepo) {
    if (config.repoType == 'git') {
      pullGit()
    } else if (config.repoType == 'ado') {
      pullAdo()
    } else if (config.repoType == 'ado-git') {
      pullAdoGit()
    }
  }
  
  //Unstash
  unstash "scripts"
  unstash "misc"
  
  //Custom result directory check and creation
  if (config.resultDirectory != null) {
    def resultDirectory = new File( "${config.resultDirectory}" )
    if (!resultDirectory.exists()) {
      resultDirectory.mkdirs()
    }
  }

  //Run the commands
  if (config.preCommand != null) {
    if (config.concealPassword) {
      runPasswordedCommand(config.precommandsType, config.preCommand)
    } else {
      runCommand(config.precommandsType, config.preCommand)
    }
  }
  if (config.command != null) {
    if (config.concealPassword) {
      runPasswordedCommand(config.commandsType, config.command)
    } else {
      runCommand(config.commandsType, config.command)
    }
  }
  if (config.postCommand != null) {
    if (config.concealPassword) {	 
      runPasswordedCommand(config.postcommandsType, config.postCommand)   
    } else {
      runCommand(config.postcommandsType, config.postCommand)
    }
  }

  //Run Text Finder
  if (config.runTextFinder) {
    findText regexp: config.textFinderRegex, alsoCheckConsoleOutput: config.textFinderCheckLog, fileSet: config.textFinderFileSet,  unstableIfFound: true
  }

  //Load properties file into config
  if (config.loadProps) {
    def propFileContent = "${config.propsFile}"
    def props = readProperties file: propFileContent
    props.each {key, value -> config[key] = value}
  }

  //Publish HTML Reports
  if (config.publishHTML) {
    publishHTML([allowMissing:false,
		 alwaysLinkToLastBuild: true,
		 keepAll: true,
		 reportDir: config.resultDirectory,
		 reportFiles: config.reportFiles,
		 reportName: config.reportName
    ])
  }

  //Archive Artifacts
  if (config.attachArchivedArtifacts) {
    archiveArtifacts artifacts: config.artifactsSet, fingerprint: true
  }

  //Publish to Confluence
  if (config.publishToConfluence) {
    publishConfluence siteName: config.siteName,
      replaceAttachments: config.replaceAttachments,
      spaceName: config.spaceName,
      pageName: config.pageName,
      attachArchivedArtifacts: config.attachArchivedArtifacts,
      buildIfUnstable: config.buildIfUnstable,
      fileSet: config.fileSet
  }

  //J Unit
  if (config.publishJunit) {
    junit config.junitSet
  }

  //Publish Robot Results
  if (config.publishRobot) {
    robot onlyCritical: config.onlyCritical,
      outputPath: config.outputPath,
      otherFiles: config.otherFiles,
      passThreshold: config.passThreshold,
      unstableThreshold: config.unstableThreshold
  }

  //Add envars and config into replaces map
  envMap = [:]
  env.getEnvironment().each { k, v -> envMap["env." + k ] = v }
  replaces = combineMaps(config, envMap)
  replaces ["env.WORKSPACE"] = env.WORKSPACE
  replaces = flattenMap(replaces)

  //Send email
  if (config.sendEmail) {
    if (config.useEmailTemplate) {
      sendEmailTemplate()
    } else {
      sendEmail()
    }
  }
}

def call (Map args) {
  node ('default') {	
    // Read the default key-value pairs from the 'global' markup
    def globalDataFile = libraryResource 'jctr/global.yml'
    def globalJson = readYaml text: globalDataFile	
    def globalInfo = [:] << globalJson.GLOBAL_SETTINGS

    // Parse and verify arguments
    if (args.appteam != null) {
      appteam = args.appteam
    } else {
      error("No appteam defined in arguments!")
    }
    if (args.suite != null) {
      suite = args.suite
    } else {
      error("No suite defined in arguments!")
    }
    if (args.test != null) {
      test = args.test
    } else {	    
      error("No test defined in arguments!")
    }
    if (args.jctrBranch != null) {
      globalInfo.jctrBranch = args.jctrBranch
    }

    // Pull the appteam-specific jctr repo and read the markup
    dir ("jctr_repos/${appteam}") {
      git branch: globalInfo.jctrBranch, credentialsId: globalInfo.credentialsId, url: "ssh://git@git.transunion.com:7999/jctr/${appteam}.git"
      appteamJson = readYaml file: "markup.yml"
    }

    // Verify the suite/test exists in the markup
    if (appteamJson.common.suites."${suite}" == null) {
      error("${suite} was not found in ${appteam}'s markup file!")
    } else if (appteamJson.common.suites."${suite}".tests."${test}" == null) {
      error("${test} was not found in the ${suite} suite of ${appteam}'s markup file!")
    }

    // Read the key-value pairs from appteam's markup, and flatten it to a map
    def appteamInfo = [:] << appteamJson.common
    appteamInfo.remove("suites")
    def suiteInfo = [:] << appteamJson.common.suites."${suite}"
    suiteInfo.remove("tests")
    def testInfo = [:] << appteamJson.common.suites."${suite}".tests."${test}"

    // Find arguments that aren't the default four, and add them to list of custom parameters
    def customparams = [:]
    args.each{	    
      key, val -> if (!['appteam', 'suite', 'test', 'userVars'].contains(key)) {
	customparams[key] = val
      }
    }

    // Add in any custom parameters from the pipeline as well
    args.userVars.each{key, val ->
      customparams[key] = val
    }
    
    // Build the config map, overwriting via hierarchy of dominance (and add custom parameters)
    config = combineMaps (globalInfo, appteamInfo, suiteInfo, testInfo, args)
    config['customparams'] = customparams
    
    // Print job details
    println ">> Running the ${test} test under ${suite} suite for ${appteam} with the following config:"
    
    // Stash any scripts or email templates
    def scriptTypes = ['script-sh', 'script-bat', 'script-python']
    dir ("jctr_repos/${config.appteam}/scripts") {
      stash allowEmpty: true, includes: "*", name: 'scripts'
    }
    dir ("jctr_repos/${config.appteam}/misc") {
      stash allowEmpty: true, includes: "*", name: 'misc'
    }
  }

  
  node (config.agent) {	
    if (config.sshagent != null) {	    
      sshagent([config.sshagent]) {
	nodeProcess()
      }
    } else {
      nodeProcess()
    }
  }
}
