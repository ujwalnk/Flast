import 'package:flutter/material.dart';

// Screens
import 'package:flast/screens/home.dart';
import 'package:flast/screens/settings.dart';
import 'package:flast/screens/splash.dart';

void main() {

  runApp(MaterialApp(
    initialRoute: "/splash",

    routes: {
      "/home": (context) => const Home(),
      "/settings": (context) => const Settings(),
      "/splash": (context) => const Splash(),
    },
    theme: ThemeData(
      useMaterial3: true,
      primaryColor: Colors.green,
    ),
    themeMode: ThemeMode.system,
    debugShowCheckedModeBanner: false,
  ));
}
