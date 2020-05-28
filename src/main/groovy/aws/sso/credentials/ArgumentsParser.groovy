package aws.sso.credentials

class ArgumentsParser {
    final String[] args

    ArgumentsParser(String... args) {
        this.args = args
    }

    boolean shouldSkipProgrammaticLogin() {
        return findArgByName("skipProg") != null
    }

    boolean shouldSkipLoginToConsole() {
        return findArgByName("skipConsole") != null
    }

    String getSSOConsoleUrl() {
        def ssoConsoleUrlFile = new File('ssoConsoleUrl.txt')
        if (ssoConsoleUrlFile.exists()) {
            def ssoConsoleUrl = ssoConsoleUrlFile.text
            if (ssoConsoleUrl && (ssoConsoleUrl.startsWith('http') || ssoConsoleUrl.startsWith('https'))) {
                return ssoConsoleUrl
            }
        }
        return null
    }

    boolean doNotCloseConsole() {
        findArgByName("leaveConsoleOpened") != null
    }

    String findArgByName(String name) {
        for (String arg in args) {
            if (arg == name) {
                return arg
            }
        }
        return null
    }
}
