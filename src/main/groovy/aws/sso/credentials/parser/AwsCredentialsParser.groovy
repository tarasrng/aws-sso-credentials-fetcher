package aws.sso.credentials.parser

import aws.sso.credentials.utils.LoggerResolver
import ch.qos.logback.classic.Logger
import groovy.json.JsonSlurper

class AwsCredentialsParser {
    private static Logger log = LoggerResolver.getLogger(AwsCredentialsParser.class)
    def parse() {
        def JSON = new JsonSlurper().parseText(resolveCachedCredentialsFile().text)
        log.info 'Parsing cached credentials file'
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
        log.info 'Saving credentials file'
        credentialsFile.text = credentialsContent
    }

    File resolveCredentialsFile() {
        new File(resolveAwsDirectory(), 'credentials')
    }

    File resolveCachedCredentialsFile() {
        def cachedCredsFile = resolveCacheDirectory().listFiles().sort {
            a, b -> b.lastModified() <=> a.lastModified()
        }.first()
        log.info("Last modified cached credentials file: $cachedCredsFile")
        cachedCredsFile
    }

    File resolveCacheDirectory() {
        def cacheDirectory = System.getProperty("user.home") + "/.aws/cli/cache/"
        log.info("CLI cache directory: ${cacheDirectory}")
        return new File(cacheDirectory)
    }

    File resolveAwsDirectory() {
        def awsDirectory = System.getProperty("user.home") + "/.aws/"
        log.info("AWS directory: ${awsDirectory}")
        return new File(awsDirectory)
    }
}
