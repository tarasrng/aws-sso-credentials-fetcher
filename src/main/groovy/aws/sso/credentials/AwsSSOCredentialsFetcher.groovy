package aws.sso.credentials

import aws.sso.credentials.login.AwsLoginRunner
import aws.sso.credentials.parser.AwsCredentialsParser

class AwsSSOCredentialsFetcher {

    static void main(String[] args) {
        def argsParser = new ArgumentsParser(args)

        boolean skipProgrammaticSSO = argsParser.shouldSkipProgrammaticLogin()
        boolean skipLoginToConsole = argsParser.shouldSkipLoginToConsole()
        boolean doNotCloseConsole = argsParser.doNotCloseConsole()
        String ssoUrl = argsParser.getSSOConsoleUrl()
        def loginRunner = new AwsLoginRunner()
        def credentialsManager = new AwsCredentialsParser()
        if (!skipProgrammaticSSO) {
            loginRunner.runSSOLogin()
            credentialsManager.saveCredentials(credentialsManager.parse())
        } else {
            println("Skipping programmatic login")
        }
        if (ssoUrl && !skipLoginToConsole) {
            loginRunner.runSSOLoginToConsole(ssoUrl, doNotCloseConsole)
        } else {
            println("Skipping console login - no url provided")
        }
    }
}

