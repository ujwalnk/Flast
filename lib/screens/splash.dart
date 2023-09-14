import 'package:flast/screens/settings.dart';
import 'package:flutter/material.dart';

// Screens
import 'package:flast/screens/home.dart';

// Services
import 'package:flast/services/secureStorage.dart';


class Splash extends StatefulWidget {
  const Splash({Key? key}) : super(key: key);

  @override
  _SplashState createState() => _SplashState();
}
class _SplashState extends State<Splash> {
  bool _appSetupRequired = false;

  @override
  void initState() {
    super.initState();
    _checkInit();
  }

  Future<void> _checkInit() async {
    SecureStorage ss = SecureStorage();

    try {
      String user = await ss.readSecureData('user');
      String pass = await ss.readSecureData('pass');

      if ((user.trim().isEmpty) || (pass.trim().isEmpty)) {
        debugPrint("Setup Required");
        setState(() {
          _appSetupRequired = true;
        });
      } else {
        debugPrint("|$user|$pass|");
        debugPrint("Setup done");
      }
    } catch (e) {
      debugPrint("Error: $e");
      _appSetupRequired = true;
      debugPrint("Setup required");
    }

    // Now that the Future has completed, you can decide the navigation logic.
    if (_appSetupRequired) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        debugPrint("Settings Page opening");
        Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (context) => const Settings()));
      });
    } else {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        debugPrint("Opening Home Page");
        Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (context) => const Home()));
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Image.asset("assets/icons/icon.png"),
        ),
      ),
    );
  }
}
