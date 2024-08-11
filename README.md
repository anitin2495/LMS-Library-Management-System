
# Library Management System

## Overview

This Library Management System is designed to manage the operations of a library, including tracking users, managing the checkout process, and calculating overdue fines. The system allows users to check out books and audio/video materials, renew items, and request unavailable items. The database is managed using MySQL, ensuring efficient data storage and retrieval.

## Features

1. **Unique Library Card**:  
   Each user is assigned a unique library card that remains with them for the duration of their membership.

2. **User Information Management**:  
   The library stores essential information for each user, including:
   - Name
   - Address
   - Phone number
   - Library card number

3. **Checkout and Fine Tracking**:  
   The system tracks items checked out by users, due dates, and any outstanding overdue fines.

4. **Checkout Limitations for Children**:  
   Children aged 12 and under can check out a maximum of five items at a time.

5. **Item Checkout**:  
   Users can check out the following items:
   - Books
   - Audio/video materials

6. **Checkout Duration**:  
   - Books: 3 weeks (2 weeks for current best sellers)
   - Audio/video materials: 2 weeks

7. **Overdue Fines**:  
   Overdue fines are calculated at a rate of $0.10 per item per day, with the fine capped at the item's value.

8. **Non-Circulating Items**:  
   Reference books and magazines are available for in-library use only and cannot be checked out.

9. **Item Requests**:  
   Users can request books or audio/video items that are currently checked out.

10. **Item Renewal**:  
    Users can renew items once, provided there are no outstanding requests for those items.

## Database

The system uses MySQL to manage the library's database. The database structure includes tables for users, items, checkout records, fines, requests, and return requests. The schema is defined in the `database.sql` file, which includes the necessary table creation scripts and initial data.

### Database Schema

The database schema is defined as follows:

- **Schema Name**: `library_d`
- **Tables**:
  1. **Users**: Stores user information, including library card number, name, address, phone number, age, and any outstanding fines.
  2. **Items**: Stores information about library items (books, audio, video, reference materials, magazines), including title, author, value, best-seller status, and availability.
  3. **Checkouts**: Tracks items checked out by users, including checkout date, due date, return status, request status, and renewal details.
  4. **Fines**: Stores fines associated with overdue items, including the amount and payment status.
  5. **Requests**: Tracks user requests for items currently checked out.
  6. **Return Requests**: Handles requests for returning specific items, including details of the requestor.

### Setting Up the Database

1. **Create a New MySQL Database**:
   - Use your MySQL client or command line to create a new database.
   - Example command:
     ```sql
     CREATE DATABASE library_d;
     ```

2. **Import the Schema**:
   - Import the `database.sql` file into your newly created database to set up the required tables and initial data.
   - Example command:
     ```bash
     mysql -u username -p library_d < database.sql
     ```

3. **Configure Database Connection**:  
   Update the database connection settings in the application configuration file to match your MySQL credentials.

## Installation

1. **Clone the Repository**:  
   ```bash
   git clone https://github.com/anitin2495/LMS-Library-Management-System.git
   ```

2. **Set Up MySQL Database**:
   - Follow the steps in the **Database** section to set up the MySQL database.

3. **Run the Application**:  
   Execute `LibrarySystem.java` to start the application.

## Login Information

- **Username**: `librarian`
- **Password**: `password`

To log in, run the `LibrarySystem.java` file and enter the above username and password.

## Usage

1. **User Management**:  
   Add, update, or remove users from the system.

2. **Item Management**:  
   Add new books and audio/video materials, update existing records, or remove items.

3. **Checkout and Return Items**:  
   Track which items users have checked out and manage the return process.

4. **Renew Items**:  
   Users can renew items once if there are no pending requests.

5. **Request Items**:  
   Users can request items currently checked out by others.

6. **Calculate Fines**:  
   Automatically calculate overdue fines for items not returned by the due date.

## Contributing

Contributions are welcome! Please submit a pull request or open an issue to discuss any changes or improvements.

---

This README provides an overview of the Library Management System, its features, installation instructions, usage guidelines, database setup, and login information.
