import 'package:flutter/material.dart';

// Screens
import 'package:flast/screens/home.dart';
import 'package:flast/screens/settings.dart';

void main() {

  runApp(MaterialApp(
    initialRoute: "/home",

    routes: {
      "/home": (context) => const Home(),
      "/settings": (context) => const Settings(),
    },
    theme: ThemeData(
      useMaterial3: true,
      primaryColor: Colors.green,
    ),
    themeMode: ThemeMode.system,
    debugShowCheckedModeBanner: false,
  ));
}
