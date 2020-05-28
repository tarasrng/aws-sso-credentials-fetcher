AWS SSO Credentials Fetcher 
=========================
### Automates retrieving of AWS SSO credentials that should be re-newed every 12 hours for CLI and programmatic access.



Runs `aws sso login` command (_to cache access token_), and then `aws sts get-caller-identity` (_to cache access keys and session token_), grabs cached keys from **[user-folder]/.aws/cli/cache/** and copies them to **[user-folder]/.aws/credentials** file.


If user doesn't click **Sign in to AWS CLI** button it will click it automatically and close a tab, so the app can be scheduled and run silently.

#### !Note

Default profile is used

Currently, clicking on Auth button automatically is implemented for Chrome browser only

#### Prerequisites:

- Java
- AWS CLI V2
- AWS SSO has to be configured by running `aws configure sso` - **profile name has to be `default`**

**Build and run:**
*  gradle uberjar
*  java -jar build/libs/aws-sso-credentials-fetcher-1.0.jar
*  To login to AWS console as well, specify the url that will be opened automatically in `ssoConsoleUrl.txt` file
*  The url can be found on sso portal page
*  To leave console opened after login to console use `leaveConsoleOpened` param
*  To skip opening log in to console use `skipConsole` param
*  To skip programmatic log in to console use `skipProg` param
