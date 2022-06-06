from cryptography.fernet import Fernet

def encrypt(s):
    # try:
    generate_key()
    fernet = get_fernet()
    shadow_file = open(".shadow", "w")
    print(fernet.encrypt(s).decode(), file=shadow_file)
    print("Credentials stored securely")
    return True
    # except Error:
    # print("Error Encountered...")
    # return False

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





