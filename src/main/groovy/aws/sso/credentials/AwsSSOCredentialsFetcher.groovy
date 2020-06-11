package aws.sso.credentials

import aws.sso.credentials.login.AwsLoginRunner
import aws.sso.credentials.parser.AwsCredentialsParser
import aws.sso.credentials.utils.LoggerResolver
import ch.qos.logback.classic.Logger

class AwsSSOCredentialsFetcher {
    private static Logger log = LoggerResolver.getLogger(AwsSSOCredentialsFetcher.class)

    static void main(String[] args) {
        log.info('-------------Starting AWS credentials fetching process-------------')
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
            log.info("Skipping programmatic login")
        }
        if (ssoUrl && !skipLoginToConsole) {
            loginRunner.runSSOLoginToConsole(ssoUrl, doNotCloseConsole)
        } else {
            log.info("Skipping console login - no url provided")
        }
    }
}

