import encryption as e

def get_driver():
    """Function to get the required driver for the application"""
    import os, glob
    global driver

    if(config["set_browser"]["use_driver"].lower() == "true"):

        # Scan for files in Current Directory
        try:
            if(is_linux()):
                driver_file = glob.glob("*driver")[0]
                if (driver_file.lower().find("chrome") != -1):
                    service = Service("./" + driver_file)
                    driver = webdriver.Chrome(service=service)
                elif (driver_file.lower().find("gecko") != -1):
                    service = Service("./" + driver_file)
                    driver = webdriver.Firefox(service=service)

            else:
                driver_file = glob.glob("*driver.exe")[0]
                if (driver_file.lower().find("chrome") != -1):
                    service = Service(".\\" + driver_file)
                    driver = webdriver.Chrome(service=service)
                elif (driver_file.lower().find("gecko") != -1):
                    service = Service(".\\" + driver_file)
                    driver = webdriver.Firefox(service=service)
        except IndexError:
            print("Driver Not Found")
    else:
        browser = config["set_browser"]["browser"].lower()
        if(browser == "chrome"):
            from webdriver_manager.chrome import ChromeDriverManager
            driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        elif(browser == "edge"):
            from webdriver_manager.microsoft import EdgeChromiumDriverManager
            driver = webdriver.Edge(service=Service(EdgeChromiumDriverManager().install()))
        elif(browser == "firefox"):
            from webdriver_manager.firefox import GeckoDriverManager
            driver = webdriver.Firefox(service=Service(GeckoDriverManager().install()))

def is_linux():
    """Check User Platform"""

    from sys import platform

    # Check if the working platform is linux
    if(platform.find("linux") != -1):
        return True

    return False

def get_config():
    import xmltodict
    global config
    try:
        with open("config.xml", "r") as conf_file:
            config = conf_file.read()
            conf_file.close()
    except FileNotFoundError:
        import config_data
        config_data.print_config_file()
        with open("config.xml", "r") as conf_file:
            config = conf_file.read()
            conf_file.close()
    
    config = xmltodict.parse(config)["options"]

try:
    # Check for secret.key file
    # Open file to access username and password
    shadow_file = open(".shadow", "r")
    key_file = open(".key", "r")

    # Use the driver to open the browser
    from selenium import webdriver
    from selenium.webdriver.common.by import By
    from selenium.webdriver.chrome.service import Service
    import json
    get_config()
    get_driver()
    global driver, config

    # # URL for the login portal
    url = "http://192.168.254.1:8090/httpclient.html"
    driver.get(url)

    # # Enter the user details
    text_user_id = driver.find_element(By.ID, "username")
    text_pswd = driver.find_element(By.ID, "password")

    if(config["hide_user_id"].lower() == "true"):
        driver.execute_script("document.getElementById(\"username\").type=\"password\";")


    secret = json.loads(e.decrypt().replace("\'", "\""))

    text_user_id.send_keys(secret["ID"])
    text_pswd.send_keys(secret["PSWD"])

    shadow_file.close()
    key_file.close()


    # # Click the the login button
    button_login = driver.find_element(By.ID, "loginbutton")
    button_login.click()

    if(config["close_window"].lower() != "false"):
        import time
        time.sleep(int(config["close_window"]))
        driver.close()

except FileNotFoundError:
    print("-----------------------Initial Run-----------------------")

    text_user = input("Enter UserName: ")
    text_pswd = input("Enter Password: ")
    
    # Get Working browser if using Windows
    e.encrypt(str({"ID": text_user, "PSWD": text_pswd}).encode())
    print("-----------------------Setup  Done-----------------------")


finally:
    print("Done")
