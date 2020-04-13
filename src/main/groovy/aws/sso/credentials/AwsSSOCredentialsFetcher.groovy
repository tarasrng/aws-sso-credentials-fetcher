package aws.sso.credentials

import aws.sso.credentials.login.AwsLoginRunner
import aws.sso.credentials.parser.AwsCredentialsParser

class AwsSSOCredentialsFetcher {

    static void main(String[] args) {
        def loginRunner = new AwsLoginRunner()
        def credentialsManager = new AwsCredentialsParser()
        loginRunner.runSSOLogin()
        credentialsManager.saveCredentials(credentialsManager.parse())
    }
}

