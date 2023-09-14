import 'package:flutter/material.dart';
import 'dart:io';

import 'package:http/http.dart' as http;
import 'package:http/io_client.dart';

// Services
import 'package:flast/services/secureStorage.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  static const String connected_string = "Connected!";
  static const String connecting_string = "Connecting";
  static const String fail_connect_string = "Sorry! Unable to Connect";

  static const String disconnected_string = "Disconnected!";
  static const String disconnecting_string = "Disconnecting";
  static const String fail_disconnect_string = "Sorry! Unable to Disconnect";

  bool connectionStatus = false;
  bool switchState = false;
  String status = disconnected_string;

  bool _setup = true;

  /// Test the connection of the device by getting data from https://raw.githubusercontent.com/ujwalnk/Flast/main/docs/success.txt
  /// Update the variable connectionStatus based on the connection Status
  /// Fail: Toggle the Button to OFF State
  /// Success: Toggle the Button to ON State
  Future<bool> testConnection() async {
    final url = Uri.parse(
        'https://raw.githubusercontent.com/ujwalnk/Flast/main/docs/success.txt');
    try {
      final response = await http.get(url);

      // TODO: Log the below information
      debugPrint(
          "Response: sc: ${response.statusCode}, body: ${response.body.trim()}, condition: ${(response.statusCode == 200) && (response.body.trim() == "success")}");

      if ((response.statusCode == 200) && (response.body.trim() == "success")) {
        // Connection Success
        connectionStatus = true;

        setState(() {
          switchState = true;
          if (status == disconnecting_string) {
            debugPrint("Status: $fail_disconnect_string");
            status = fail_disconnect_string;
          } else {
            status = connected_string;
            debugPrint("Status: $connected_string");
          }
        });
      } else {
        // Connection Fail
        connectionStatus = false;

        setState(() {
          switchState = false;
          if (status == connecting_string) {
            status = fail_connect_string;
            debugPrint("Status: $fail_connect_string");
          } else {
            status = disconnected_string;
            debugPrint("Status: $disconnected_string");
          }
        });
      }
    } on Exception catch (_) {
      setState() {
        switchState = false;
        status = fail_connect_string;
        debugPrint("Status: $fail_connect_string");
        debugPrint("$_ caught when on connecting to success.txt");
      }
    }

    return connectionStatus;
  }

  Future post({bool connect = true}) async {
    SecureStorage ss = SecureStorage();
    try {
      final ioc = HttpClient();
      ioc.badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
      final http = IOClient(ioc);
      http.post(Uri.parse("https://192.168.254.1:8090/login.xml"), body: {
        'mode': connect ? "191" : "193",
        "username": await ss.readSecureData("user"),
        "password": await ss.readSecureData("pass"),
        "a": DateTime.now().millisecondsSinceEpoch.toString(),
        "producttype": "0",
      }).then((response) {
        debugPrint("Reponse status : ${response.statusCode}");
        debugPrint("Response body : ${response.body}");
        if (response.body.contains(
            "Login failed. You have reached the maximum login limit.")) {
          setState() {
            status = "Sorry! Maximum Login Limit Reached!";
          }
        }
      });
    } catch (e) {
      debugPrint("@Post2: Error Encountered: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    // Get the width of the screen and get size as 30% of width maximum of 120
    double switchWidth = MediaQuery.of(context).size.width * 0.3 > 120
        ? 120
        : MediaQuery.of(context).size.width * 0.3;
    double paddingHeight = MediaQuery.of(context).size.height * 0.2;

    if (_setup) {
      _setup = false;
      testConnection();
    }

    return Scaffold(
        appBar: AppBar(
          title: const Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Flast',
                style:
                    TextStyle(fontSize: 24.0), 
              ),
              Text(
                'by Madilu',
                style: TextStyle(fontSize: 12.0),
              ),
            ],
          ),
          centerTitle: false,
          automaticallyImplyLeading: false,
          actions: <Widget>[
            IconButton(
              icon: const Icon(Icons.settings),
              onPressed: () {
                Navigator.pushNamed(context, "/settings");
              },
            ),
          ],
        ),
        body: Center(
          child: Column(children: <Widget>[
            SizedBox(height: paddingHeight * 1.5),
            Transform.scale(
              scale: switchWidth / 45,
              child: Switch(
                value: switchState,
                onChanged: (bool value) {
                  setState(() {
                    // Switch position and variable handling
                    switchState = value;
                    if (value == true) {
                      // Login
                      post();
                      status = connecting_string;
                    } else {
                      post(connect: false);
                      status = disconnecting_string;
                    }

                    // Test for successful connection
                    testConnection();
                  });
                },
              ),
            ),
            SizedBox(
              height: paddingHeight / 4,
            ),
            Visibility(
              visible: status.isNotEmpty,
              child: Text(status),
            )
          ]),
        ));
  }
}
