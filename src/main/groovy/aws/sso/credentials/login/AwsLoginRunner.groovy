package aws.sso.credentials.login

import java.awt.*

class AwsLoginRunner {
    private final static long MAX_SSO_LOGIN_WAIT_TIME_MILLIS = 90_000
    private final static long WAIT_TIME_BEFORE_AUTO_CLICKING_AUTH_BUTTON_MILLIS = 15_000
    private final static long WAIT_TIME_BEFORE_AUTO_CLOSING_CONSOLE_MILLIS = 15_000
    private final static long PAGE_CHECKING_RETRY_DELAY_MILLIS = 8_000

    def runSSOLogin() {
        def executionOutput = new StringBuilder(), executionErrors = new StringBuilder()
        println 'Executing aws sso login to cache access token'
        def proc = 'aws sso login'.execute()
        proc.consumeProcessOutput(executionOutput, executionErrors)

        def clickAuthorizeButtonTask = Thread.start {
            clickAuthorizeInBrowser()
        }

        proc.waitForOrKill(MAX_SSO_LOGIN_WAIT_TIME_MILLIS)
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

    def runSSOLoginToConsole(String ssoConsoleUrl, boolean doNotCloseConsole) {
        openWebpage(new URL(ssoConsoleUrl).toURI())
        if (doNotCloseConsole) {
            return
        }
        boolean interrupted = false

        sleep(WAIT_TIME_BEFORE_AUTO_CLOSING_CONSOLE_MILLIS) { e ->
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
        boolean pageIsCorrect = chromeController.checkIfConsolePageIsCorrect()
        if (!pageIsCorrect) {
            chromeController.toggleConsole()
            Thread.sleep(PAGE_CHECKING_RETRY_DELAY_MILLIS)
            chromeController.toggleConsole()
            pageIsCorrect = chromeController.checkIfConsolePageIsCorrect()
        }
        if (!pageIsCorrect) {
            println 'Few page checks failed. Can\'t close tab. '
            return
        }
        chromeController.toggleConsole()
        chromeController.closeCurrentTab()
    }

    static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri)
                return true
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        return false
    }

    private def clickAuthorizeInBrowser() {
        boolean interrupted = false
        sleep(WAIT_TIME_BEFORE_AUTO_CLICKING_AUTH_BUTTON_MILLIS) { e ->
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
        boolean pageIsCorrect = chromeController.checkIfAuthPageIsCorrect()
        if (!pageIsCorrect) {
            chromeController.toggleConsole()
            Thread.sleep(PAGE_CHECKING_RETRY_DELAY_MILLIS)
            chromeController.toggleConsole()
            pageIsCorrect = chromeController.checkIfAuthPageIsCorrect()
        }
        if (!pageIsCorrect) {
            println 'Few page checks failed. Can\'t automate authorize button click. '
            return
        }
        chromeController.clickAuthButton()
        chromeController.toggleConsole()
        chromeController.closeCurrentTab()
    }
}
