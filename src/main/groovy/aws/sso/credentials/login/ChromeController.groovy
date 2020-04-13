package aws.sso.credentials.login

import org.apache.commons.lang3.SystemUtils

import java.awt.*
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent

class ChromeController {
    private static final int COMMAND_OR_CONTROL = SystemUtils.IS_OS_MAC ? KeyEvent.VK_META : KeyEvent.VK_CONTROL
    private static final int OPTIONS_OR_SHIFT = SystemUtils.IS_OS_MAC ? 58 : KeyEvent.VK_SHIFT

    private boolean isConsoleOpened
    private Robot robot = new Robot()

    def toggleConsole() {
        def action = isConsoleOpened ? 'close' : 'open'
        println "Trying to $action chrome console"
        robot.keyPress(COMMAND_OR_CONTROL)
        robot.keyPress(OPTIONS_OR_SHIFT)
        robot.keyPress(KeyEvent.VK_J)

        robot.keyRelease(KeyEvent.VK_J)
        robot.keyRelease(OPTIONS_OR_SHIFT)
        robot.keyRelease(COMMAND_OR_CONTROL)
        Thread.sleep(1000)
        isConsoleOpened = !isConsoleOpened
        if (isConsoleOpened) {
            makeConsoleEditable()
        }
    }

    def clickAuthButton() {
        println 'Trying to click authorize button'
        String buttonPressJQuery = "\$('#cli_login_button').click()"
        StringSelection stringSelection = new StringSelection(buttonPressJQuery)
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(stringSelection, stringSelection)

        robot.keyPress(COMMAND_OR_CONTROL)
        robot.keyPress(KeyEvent.VK_V)
        robot.keyRelease(KeyEvent.VK_V)
        robot.keyRelease(COMMAND_OR_CONTROL)
        Thread.sleep(100)
        robot.keyPress(KeyEvent.VK_ENTER)
        robot.keyRelease(KeyEvent.VK_ENTER)
        Thread.sleep(1000)
    }

    def closeCurrentTab() {
        println 'Trying to close current tab'
        robot.keyPress(COMMAND_OR_CONTROL)
        robot.keyPress(KeyEvent.VK_W)
        robot.keyRelease(KeyEvent.VK_W)
        robot.keyRelease(COMMAND_OR_CONTROL)
    }

    private def makeConsoleEditable() {
        println 'Trying to make console editable'
        robot.keyPress(COMMAND_OR_CONTROL)
        robot.keyPress(KeyEvent.VK_CLOSE_BRACKET)

        robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET)
        robot.keyRelease(COMMAND_OR_CONTROL)
        Thread.sleep(500)
        robot.keyPress(COMMAND_OR_CONTROL)
        robot.keyPress(KeyEvent.VK_OPEN_BRACKET)

        robot.keyRelease(KeyEvent.VK_OPEN_BRACKET)
        robot.keyRelease(COMMAND_OR_CONTROL)
        Thread.sleep(400)
    }

    boolean checkIfPageIsCorrect() {
        String copyCheckResultToClipboardJQuery = """
var test = \$('#cli_login_button') != null;
 var dummy = document.createElement("textarea");
    document.body.appendChild(dummy);
    dummy.value = test;
    dummy.select();
    document.execCommand("copy");
    document.body.removeChild(dummy);
"""
        println 'Checking the page'

        StringSelection stringSelection = new StringSelection(copyCheckResultToClipboardJQuery)
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(stringSelection, stringSelection)

        robot.keyPress(COMMAND_OR_CONTROL)
        robot.keyPress(KeyEvent.VK_V)
        robot.keyRelease(KeyEvent.VK_V)
        robot.keyRelease(COMMAND_OR_CONTROL)
        Thread.sleep(100)
        robot.keyPress(KeyEvent.VK_ENTER)
        robot.keyRelease(KeyEvent.VK_ENTER)
        Thread.sleep(200)

        String clipboardData = (String) Toolkit.getDefaultToolkit()
                .getSystemClipboard().getData(DataFlavor.stringFlavor)
        def result = 'true' == clipboardData
        println("On the right auth page - $result")
        result
    }

}
