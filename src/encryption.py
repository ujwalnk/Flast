import logging
from cryptography.fernet import Fernet

def encrypt(s):
    try:
        # Generate key
        generate_key()
        fernet = get_fernet()

        # Write the key to a file
        shadow_file = open(".shadow", "w")
        print(fernet.encrypt(s).decode(), file=shadow_file)

        print("Credentials stored securely")
        return True

    except Exception as e:
        # Write the exception to log file
        print("Error Encountered...")
        logging.exception(e)
        
        return False

def generate_key():
    # Key Generation
    key = Fernet.generate_key()

    # String the key in a file
    secret = open(".key", "wb")
    secret.write(key)
    secret.close()

    print("Key File Generated")

def get_fernet():
    # Read the key from the file
    secret = open(".key", "rb")
    key = secret.read()
    secret.close()

    return Fernet(key)

def decrypt():
    # Get Fernet Object
    fernet = get_fernet();

    # Get the encrypted data
    shadow_file = open(".shadow", "r")
    shadow_text = shadow_file.read()
    return fernet.decrypt(shadow_text.encode()).decode()
