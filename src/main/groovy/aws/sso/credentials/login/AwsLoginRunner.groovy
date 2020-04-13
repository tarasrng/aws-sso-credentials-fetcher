package aws.sso.credentials.login

class AwsLoginRunner {
    def runSSOLogin() {
        def executionOutput = new StringBuilder(), executionErrors = new StringBuilder()
        println 'Executing aws sso login to cache access token'
        def proc = 'aws sso login'.execute()
        proc.consumeProcessOutput(executionOutput, executionErrors)

        def clickAuthorizeButtonTask = Thread.start {
            clickAuthorizeInBrowser()
        }

        proc.waitForOrKill(30_000)
        //wait for click task to finish
        Thread.sleep(4_000)
        clickAuthorizeButtonTask.interrupt()
        println 'AWS CLI execution output:'
        println(executionOutput)
        if (!executionErrors.toString().isEmpty()) {
            throw new RuntimeException("aws so login command failed. ${executionErrors.toString()}")
        }
        println 'Executing aws sts get-caller-identity to cache access keys and session token'
        proc = 'aws sts get-caller-identity'.execute()
        executionOutput = new StringBuilder()
        executionErrors = new StringBuilder()
        proc.consumeProcessOutput(executionOutput, executionErrors)
        proc.waitForOrKill(5_000)
    }

    private def clickAuthorizeInBrowser() {
        boolean interrupted = false
        sleep(10_000) { e ->
            assert e in InterruptedException
            println 'Authorized button was pressed manually'
            interrupted = true
            true
        }
        if (interrupted) {
            return
        }
        def chromeController = new ChromeController()
        chromeController.toggleConsole()
        chromeController.clickAuthButton()
        chromeController.toggleConsole()
        chromeController.closeCurrentTab()
    }
}
