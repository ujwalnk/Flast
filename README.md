# AutoICP
Simple Python Automation to automate logging into PESU Internet Portal

# Minimum Requirements
1. Python 3.xx
2. Browser (Chrome, Firefox)
3. Login Credentials

# Getting Started
1. Run the app on the terminal
2. Enter the user name and password
3. Details will be encrypted and stored in your local directory
4. Edit the configuration file
5. Run the app once more

# Editing the Configuration File
- `hide_user_id`: Will hide the user ID, converting the name field to password field
- `close_window`: Close window once the login button is clicked 
  - `false`: Leave the window open
  - `(int)`: Close the window after delay
- `open_popup`:  Experimental
  - `false` : Do not open the google popup tab
  - `true`  : Open the google popup tab
- `set_browser`: 
  - `use_driver`: Download the driver to the local folder
    -  `true`: Search for the driver in the local folder and chose the first one
    - `false`: Use the previously chaced driver (Experimental)
  - `browser`:
    - Uncomment the preffered browser 
    - Must be connected to the interent the first time to download the driver
    - Will use the downloaded driver in the consequitve runs

# Downloading the driver
| Browser | Driver Link |
|---|---|
| Chrome Driver | https://chromedriver.chromium.org/downloads|
| Edge Driver| https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/|
| FireFox Driver| https://github.com/mozilla/geckodriver/releases |

# Misc
- To start afresh, delete `.key` `.secret` and `config.xml` files
- `.key` - Stores the key for Encryption
- `.secret` - Stores username and password
