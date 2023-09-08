import 'package:flutter/material.dart';

// Services
import 'package:flast/services/secureStorage.dart';

class Settings extends StatefulWidget {
  const Settings({super.key});

  @override
  State<Settings> createState() => _SettingsState();
}

class _SettingsState extends State<Settings> {
  bool _isPasswordVisible = false;

  // TextFieldControllers
  final userTFC = TextEditingController();
  final passTFC = TextEditingController();

  SecureStorage ss = SecureStorage();

  String _initialUser = "";
  String _initialPass = "";

  @override
  void dispose() {
    // Clean up the controller when the widget is disposed.
    userTFC.dispose();
    passTFC.dispose();
    super.dispose();
  }


  void getData() async{
    try{
      _initialUser = await ss.readSecureData("user");
    } on Exception catch(_){
      _initialUser = "";
      _initialPass = "";
    }
    _initialUser = _initialUser ?? " ";

    _initialPass = await ss.readSecureData("user");
    _initialPass = _initialPass ?? " ";

  }

  @override
  Widget build(BuildContext context) {

    // Get initial data if stored in the sharedPref
    getData();

    return Scaffold(
        appBar: AppBar(title: const Text("Settings")),
        body: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            children: <Widget>[
              TextFormField(
                controller: userTFC,
                decoration: const InputDecoration(
                  labelText: "Username",
                  border: OutlineInputBorder(),
                ),
                // TODO: Initial Value not working, check
                // initialValue: _initialUser ?? " ",
              ),
              const SizedBox(height: 16),
              TextFormField(
                obscureText: !_isPasswordVisible,
                controller: passTFC,
                decoration: InputDecoration(
                  labelText: "Password",
                  border: const OutlineInputBorder(),
                  suffixIcon: IconButton(
                    icon: Icon(_isPasswordVisible
                        ? Icons.visibility
                        : Icons.visibility_off),
                    onPressed: () {
                      setState(() {
                        _isPasswordVisible = !_isPasswordVisible;
                      });
                    },
                  ),
                ),
                // TODO: Check initialValue not working
                // initialValue: _initialPass ?? "something" 
              ),
              const Spacer(),
              FilledButton(
                onPressed: () async {
                  debugPrint("@Settings: Saving data to Storage");
                  // Save the username and password to the SecureStorage
                  ss.writeSecureData("user", userTFC.text);
                  ss.writeSecureData("pass", passTFC.text);

                  // TODO: Remove the below print statements
                  debugPrint("Writing > UserID: ${userTFC.text}, Pass: ${passTFC.text}");
                  debugPrint("Reading > UserID: ${await ss.readSecureData("user")}, Pass: ${await ss.readSecureData("pass")}");

                  Navigator.pop(context);
                },
                child: const Text("Save"),
              ),
            ],
          ),
        ));
  }
}
