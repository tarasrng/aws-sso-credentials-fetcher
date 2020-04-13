package aws.sso.credentials.parser

import groovy.json.JsonSlurper

class AwsCredentialsParser {

    def parse() {
        def JSON = new JsonSlurper().parseText(resolveCachedCredentialsFile().text)
        println 'Parsing cached credentials file'
        def credentials = JSON.Credentials
        if (!credentials) {
            throw new RuntimeException("Could't parse credentials. " +
                    "Make sure the file is a JSON that contains Credentials object.")
        }
        String accessKeyId = credentials.AccessKeyId
        String secretAccessKey = credentials.SecretAccessKey
        String sessionToken = credentials.SessionToken

        "[default]\n" +
                "aws_access_key_id = $accessKeyId\n" +
                "aws_secret_access_key = $secretAccessKey\n" +
                "aws_session_token = $sessionToken"
    }

    def saveCredentials(String credentialsContent) {
        def credentialsFile = resolveCredentialsFile()
        println 'Saving credentials file'
        credentialsFile.text = credentialsContent
    }

    File resolveCredentialsFile() {
        new File(resolveAwsDirectory(), 'credentials')
    }

    File resolveCachedCredentialsFile() {
        def cachedCredsFile = resolveCacheDirectory().listFiles().sort {
            a, b -> b.lastModified() <=> a.lastModified()
        }.first()
        println("Last modified cached credentials file: $cachedCredsFile")
        cachedCredsFile
    }

    File resolveCacheDirectory() {
        def cacheDirectory = System.getProperty("user.home") + "/.aws/cli/cache/"
        println("CLI cache directory: ${cacheDirectory}")
        return new File(cacheDirectory)
    }

    File resolveAwsDirectory() {
        def awsDirectory = System.getProperty("user.home") + "/.aws/"
        println("AWS directory: ${awsDirectory}")
        return new File(awsDirectory)
    }
}
