package aws.sso.credentials

import org.apache.commons.lang3.StringUtils

class ArgumentsParser {
    final String[] args

    ArgumentsParser(String... args) {
        this.args = args
    }

    boolean shouldSkipProgrammaticLogin() {
        return findArgByName("skipProg") != null
    }

    String getSSOConsoleUrl() {
        String ssoConsoleUrl = findArgByName("ssoConsoleUrl", true)
        if (!ssoConsoleUrl || !ssoConsoleUrl.contains("=")) {
            return null
        } else {
            return StringUtils.substringAfter(ssoConsoleUrl, "=")
        }
    }

    boolean doNotCloseConsole() {
        findArgByName("leaveConsoleOpened") != null
    }

    String findArgByName(String name, boolean startsWith = false) {
        for (String arg in args) {
            boolean found = startsWith ? arg.startsWith(name) : arg == name
            if (found) {
                return arg
            }
        }
        return null
    }
}
