from audioop import reverse
from cmath import log
import gc
import encryption as e
import logging 
logging.basicConfig(filename='.log', level=logging.INFO, format='%(asctime)s %(levelname)s: %(message)s', datefmt='%m/%d/%Y %I:%M:%S %p')


def download_driver(browser):
    """
    Download webdriver for selenium automation from the internet

    Parameters:
        browser: Browser selection (Chrome, Firefox, Edge)

    Return:
        NULL
    """

    if(browser == "chrome"):
        from webdriver_manager.chrome import ChromeDriverManager
        import_drivers("Chrome")
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
    
    elif(browser == "edge"):
        from webdriver_manager.microsoft import EdgeChromiumDriverManager
        driver = webdriver.Edge(service=Service(EdgeChromiumDriverManager().install()))
    
    elif(browser == "firefox"):
        import_drivers("Gecko")
        from webdriver_manager.firefox import GeckoDriverManager
        driver = webdriver.Firefox(service=Service(GeckoDriverManager().install()))

def get_config():
    """Read data from config.xml file"""

    import xmltodict
    global config

    # Open config file
    try:
    
        with open("config.xml", "r") as conf_file:
            config = conf_file.read()
            conf_file.close()
    
    # If Config file not found create it and read it
    except FileNotFoundError:
    
        import config_data
        config_data.print_config_file()

        print("Config file not found, creating default config.xml file")
        logging.warning("Config FileNotFoundError: Creating new file and using default values")
    
        with open("config.xml", "r") as conf_file:
            config = conf_file.read()
            conf_file.close()
    
    # Read data 
    config = xmltodict.parse(config)["options"]
    logging.info(config)

def get_driver():
    """Get required drivers based on application"""

    import os, glob
    global driver
    ghost_mode = False

    # Check for ghost mode > Headless mode
    if(config["ghost"].lower() == "true"):
        ghost_mode = True

    logging.info("Ghost Mode: " + str(ghost_mode))

    if(config["set_browser"]["use_driver"].lower() == "true"):
        # Scan for files in Current Directory
        try:

            if(is_linux()):

                driver_file = glob.glob("*driver")[0]

                if (driver_file.lower().find("chrome") != -1):

                    import_drivers("Chrome")

                    option = Options()
                    option.headless = ghost_mode
                    driver = webdriver.Chrome(service=Service("./" + driver_file), options=option)
                
                elif (driver_file.lower().find("gecko") != -1):

                    import_drivers("Gecko")

                    option = Options()
                    option.headless = ghost_mode
                    driver = webdriver.Firefox(service=Service("./" + driver_file), options=option)

            else:
                driver_file = glob.glob("*driver.exe")[0]
                
                if (driver_file.lower().find("chrome") != -1):
                    import_drivers("Chrome")
                    
                    option = Options()
                    option.headless = ghost_mode
                    driver = webdriver.Chrome(service=Service(".\\" + driver_file), options=option)

                elif (driver_file.lower().find("gecko") != -1):
                    import_drivers("Gecko")

                    option = Options()
                    option.headless = ghost_mode
                    driver = webdriver.Firefox(service=Service(".\\" + driver_file), options=option)

        except IndexError:

            print("Driver Not Found, trying to download")
            logging.warning("Driver Not Found")
            try:
                download_driver("Chrome")
            except Exception as e:
                logging.error("Unable to download Driver, on driver not found: " + str(e))

        print("Automation in Progress")

    else:
        # Get the browser selection from the config file
        browser = config["set_browser"]["browser"].lower()

        # Download the selenium driver from the internet
        try:
            download_driver(browser)
        except Exception as e:
            logging.error("Unable to download driver: " + str(e))

def hide_user_id():
    """
    Hide User ID?
    Input:         Config File
    Parameters:    NULL
    Return:        NULL
    """

    if(config["hide_user_id"].lower() == "true"):
        logging.info("Hiding UserID Field")
        driver.execute_script("document.getElementById(\"username\").type=\"password\";")

def import_drivers(browser):
    """
    Import required driver libraries
    
    Parameters:
        browser: Browser Name (edge, gecko, chrome)
    
    Return: NULL
    """

    global Service, Options 

    # Import all modules required for browser
    if(browser.lower() == "gecko"):

        from selenium.webdriver.firefox.options import Options
        from selenium.webdriver.firefox.service import Service

        logging.info("Using Driver: Firefox")

    elif(browser.lower() == "chrome"):
        
        from selenium.webdriver.chrome.options import Options
        from selenium.webdriver.chrome.service import Service
    
        logging.info("Using Driver: Chrome")

    # elif(browser.lower() == "edge"):

def is_linux():
    """Check User Platform"""

    from sys import platform

    # Check if the working platform is linux
    if(platform.find("linux") != -1):
        return True

    return False

def open_application():
    """Open appliction at the path in config.xml file"""

    if(config["open_application"].lower() != "false"):

        # Split applications in list by newline character
        apps_to_launch = config["open_application"].split("\n")

        import os

        for app in apps_to_launch:

            app = app.lstrip()

            # Open application
            try:
                if(is_linux()):
                    # TODO: Test app launch on linux
                    os.system(app)
                else:
                    os.startfile(app)
                
                print("Application file executed: ", app)
                logging.info("Opening Application: " + str(app))

            except Exception as e:
                print("Encountered exception while opening application: ", e)
                logging.warning("Encountered exception while opening application: " + str(e))
    
def setup():
    """
    Setup Function
        Get Interet Captive Portal USER ID and PASSWORD, store it in the encrypted file
    """
    text_user = input("Enter UserName: ")
    text_pswd = input("Enter Password: ")

    logging.info("Storing UserID and Password on setup")
    e.encrypt(str({"ID": text_user, "PSWD": text_pswd}).encode())
    
    # Free Memory for security reasons
    del text_user, text_pswd

if __name__ == "__main__":
    try:
        
        # Check for secret & key file
        # Open file to access username and password
        
        shadow_file = open(".shadow", "r")
        logging.info("Shadow File Opened")
        
        key_file = open(".key", "r")
        logging.info("Key File opened")

        from selenium import webdriver
        from selenium.webdriver.common.by import By
        import json

        import get_walled_garden as gwg

        get_config()
        get_driver()

        global config, driver

        # URL for the login portal
        url = gwg.get_ip()

        # Check for Internet availability
        if(url == False):        
        
            open_application()
            input("Press Enter to exit")
            from sys import exit
            exit()

        #  Open the Walled Garden URL
        driver.get(url)

        # Enter the user details
        text_user_id = driver.find_element(By.ID, "username")
        text_pswd = driver.find_element(By.ID, "password")
        logging.info("Found text_* elements on the page")

        # Hide User ID
        hide_user_id()

        # Get password from file
        secret = json.loads(e.decrypt().replace("\'", "\""))

        # Enter the UserID and Password into the portal
        text_user_id.send_keys(secret["ID"])
        text_pswd.send_keys(secret["PSWD"])
        logging.info("Sending Keys to text_* fields")

        # Close files and free memory, for security purposes
        shadow_file.close()
        key_file.close()
        del shadow_file, key_file, secret
        
        logging.info("Shadow and Key Files Closed and removed from memory")

        # Click the the login button
        button_login = driver.find_element(By.ID, "loginbutton")
        button_login.click()
        logging.info("Login button clicked")

        import time

        try:
            # Wait for login attempt
            time.sleep(2)

            # Check for error
            driver.find_element(By.CLASS_NAME, "red").click()

            print("Incorrect Password")
            logging.warning("Incorrect Password")

            sys.exit()
        except Exception:
            print("Login Success")
            logging.info("Login Success")

        # Open application on login
        open_application()

        # Close window on login after time delay
        if(config["close_window"].lower() != "false"):
            time.sleep(int(config["close_window"]))

            driver.close()

            logging.info("Closing Driver on close_window flag")

        # Logout on Program exit
        if(config["logout"].lower() == "true") and (config["close_window"].lower() == "false"):

            logging.info("Waiting for user input to logout and exit")

            # Wait for user input to logout of the ICP
            input("Press Enter to exit")

            from selenium.webdriver.support import expected_conditions as ec
            from selenium.webdriver.support.ui import WebDriverWait
            
            # Click the logout button 
            driver.find_element(By.ID, "loginbutton").click()
            logging.info("Logout Button clicked. Waiting for UserName element")

            # Wait for the username field to be visible to ensure successful logout
            wait = WebDriverWait(driver, 25)
            men_menu = wait.until(ec.visibility_of_element_located((By.XPATH, "//*[@id=\"username\"]")))

            # Close the driver
            driver.close()
            logging.info("Closed driver on logout flag")
            from sys import exit
            exit()

    except FileNotFoundError:
        # Setup
        print("-----------------------Initial Run-----------------------")
        setup()
        print("-----------------------Setup  Done-----------------------")

        # Rerun the app to with the same arguments
        import os, sys
        os.execv(sys.argv[0], sys.argv)

    except KeyboardInterrupt:
        logging.info("Keyboard Interrupt")
        driver.close()

     except Exception as e:
         print("Fatal Error encountered:", e)
         logging.info("Fatal Error Encountered: " + str(e))
         input("Press Enter to exit")
