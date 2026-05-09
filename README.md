# Overview

The goal of this project was to create a student management system for schools to be able to manage student information and records as well as courses. This is a desktop application built in Java.

# Project Setup

All required files are in the src/ folder. There are three main classes - DatabaseConnection, which takes care of the database connection. LoginGUI - which takes care of all the login logic. StudentDataBaseGUI is the last class which presents the UI and suppoerts all operations. To set up the project, run the create_schema.sql file to generate a useable database schema. After that, run the mySQL server in command line (or wherever it is on your machine) and then run the LoginGUI.java file.

# Dependencies & Software

All that is required is a Java compiler and mySQL installed. The mySQL connector jar file is available under lib/ for use when launching this project. Depending on the operating system, a new file may be required from mySQL.

# Connecting information:

Once the schema is initialized, be sure to change the password and username in the DatabaseConnection.java file to match those of your mySQL setup. This information is important to change to ensure the application runs correctly.
