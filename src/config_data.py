def print_config_file():
    file = open("config.xml", "w")
    print("""<options>
    <hide_user_id>
        <!-- True  : Hides the User ID when inputting -->
        <!-- False : User ID will be displayed -->
        True
    </hide_user_id>
    <close_window>
        <!-- x     : Close window x seconds after login is clicked (EXPERIMENTAL) -->
        <!-- False : Keep the browser window open -->
        False
    </close_window>
    <open_popup>
        <!-- EXPERIMENTAL -->
        <!-- True  : Open the google pop-up tab -->
        <!-- False : Don't open the google pop-up tab  -->
        false
    </open_popup>
    <set_browser>
        <!-- Comment out all options inside set_browser to use driver -->
        <!-- Driver is given higher priority -->
        <use_driver>
            <!-- Set True to search for Driver in current directory, Works Offline -->
            True
        </use_driver>
        <browser>
            <!-- EXPERIMENTAL -->
            <!-- Driver for the browser will be downloaded to cache and used, might not work once the API Rate Limit is Reached, Does not work Offline -->
            <!-- Works only if previously downloaded & cached file is available -->
            <!-- Uncomment the browser of your choice -->
            <!-- Chrome -->
            <!-- Edge  -->   <!-- (EXPERIMENTAL) --> 
            <!-- Firefox -->
        </browser>
    </set_browser>
</options>""", file=file)