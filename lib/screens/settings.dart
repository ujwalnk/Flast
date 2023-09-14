import 'package:flutter/material.dart';

// Screens
import 'package:flast/screens/home.dart';

// Services
import 'package:flast/services/secureStorage.dart';

class Settings extends StatefulWidget {
  const Settings({super.key});

  @override
  State<Settings> createState() => _SettingsState();
}

class _SettingsState extends State<Settings> {
  bool _isPasswordVisible = false;

  SecureStorage ss = SecureStorage();

  String _initialUser = "";
  String _initialPass = "";

  @override
  void initState() {
    super.initState();
    // Call getData in the initState to populate initial data
    getData();
  }

  Future<void> getData() async {
    try {
      _initialUser = await ss.readSecureData("user");
    } on Exception catch (_) {
      _initialUser = "";
      _initialPass = "";
    }
    _initialUser = _initialUser ?? "";

    _initialPass = await ss.readSecureData("pass");
    _initialPass = _initialPass ?? "";

    // Call setState after data retrieval is complete
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    // TextFieldControllers
    final userTFC = TextEditingController(text: _initialUser);
    final passTFC = TextEditingController(text: _initialPass);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Settings"),
        automaticallyImplyLeading: false,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const Home())),
        ),
      ),
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
            ),
            const SizedBox(height: 16),
            TextFormField(
              obscureText: !_isPasswordVisible,
              controller: passTFC,
              decoration: InputDecoration(
                labelText: "Password",
                border: const OutlineInputBorder(),
                suffixIcon: IconButton(
                  icon: Icon(_isPasswordVisible ? Icons.visibility : Icons.visibility_off),
                  onPressed: () {
                    setState(() {
                      _isPasswordVisible = !_isPasswordVisible;
                    });
                  },
                ),
              ),
            ),
            const Spacer(),
            FilledButton.icon(
              onPressed: () async {
                debugPrint("@Settings: Saving data to Storage");
                // Save the username and password to the SecureStorage
                ss.writeSecureData("user", userTFC.text);
                ss.writeSecureData("pass", passTFC.text);

                // Navigate home
                Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const Home()));
              },
              label: const Text("Save"),
              icon: const Icon(Icons.save)
            ),
          ],
        ),
      ),
    );
  }
}
