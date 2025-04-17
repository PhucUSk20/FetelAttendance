# 🤖 FETEL ATTENDANCE
**Team size**: 3 members  
**Roles**: Leader
**Development time**: December 2023 – September 2024
FETEL Attendance is a mobile application that manages classroom attendance using Google ML Kit, OpenCV's Haar Cascade, and FaceNet for face detection and recognition. Each class is managed by a lecturer account and stores student face data separately. Students register their facial data and are recognized in real-time through video streaming. The system supports three roles: Admin, Lecturer, and Student. Students join classes using a class code. Attendance data can be exported to Excel and JSON formats.

---

## 📌 Table of Contents

- [1. Database](#1-database)
  - [1.1 ER Diagram](#11-er-diagram)
  - [1.2 Relational Diagram](#12-relational-diagram)
  - [1.3 Tables](#13-tables)
  - [1.4 JDBC Connection](#14-jdbc-connection)
- [2. AI Model (FaceNet)](#2-ai-model-facenet)
  - [2.1 Feature Extraction and Matching](#21-feature-extraction-and-matching)
  - [2.2 General Workflow](#22-general-workflow)
- [3. Application Interface](#3-application-interface)
  - [3.1 Student Interface](#31-student-interface)
  - [3.2 Lecturer Interface](#32-lecturer-interface)
  - [3.3 Admin Interface](#33-admin-interface)
- [4. Technologies Used](#4-technologies-used)
- [5. Demo](#5-demo)

---

## 1. Database

### 1.1 ER Diagram  
![ER Diagram](https://imgur.com/Ly4BVjq.png)


### 1.2 Relational Diagram
![Relational Model](https://imgur.com/Pk7hAXR.png)

### 1.3 Tables  
- **Lecturers (giangvien)**  
  ![giangvien table](https://imgur.com/vub6RoW.png)  
- **Class**  
  ![class table](https://imgur.com/W3b9rqZ.png)  
- **Student List**  
  ![student_list table](https://imgur.com/dVud0aH.png)  
- **Participation (THAMGIA)**  
  ![thamgia table](https://imgur.com/yYmhpdC.png)  
- **Attendance**  
  ![attendance table](https://imgur.com/IbInPsl.png)  

### 1.4 JDBC Connection  
![JDBC Connection](https://imgur.com/xl5Cikt.png)

---

## 2. AI Model (FaceNet)

### 2.1 Feature Extraction and Matching  
![Feature extraction and matching](https://imgur.com/uGDuEZt.png)

### 2.2 General Workflow  
![AI model workflow](https://imgur.com/OXRaFTl.png)

---

## 3. Application Interface

### 3.1 Student Interface  
- Profile setup  
  ![Student profile setup](https://imgur.com/LYGp71e.png)  
- Join class  
  ![Student join class](https://imgur.com/bCSnN8T.png)  

### 3.2 Lecturer Interface  
- Dashboard  
  ![Lecturer dashboard](https://imgur.com/hXGeEhw.png)  
- Attendance via gallery, camera, or live stream  
  ![Attendance via image options](https://imgur.com/ztRBmOa.png)  
- Real-time multi-face recognition  
  ![Multi-face attendance 1](https://imgur.com/8whQkBj.png)  
  ![Multi-face attendance 2](https://imgur.com/mf6igWF.png)  
- Export attendance (by date/class)  
  ![Export by date/class](https://imgur.com/7JjN6EM.png)  
- Export full class attendance  
  ![Export full class](https://imgur.com/SMZ8wRO.png)  

### 3.3 Admin Interface  
- Create new class  
  ![Create class](https://imgur.com/KYfXTsp.png)  
- Delete students  
  ![Delete students](https://imgur.com/vzrzfcV.png)  
- Add student to class  
  ![Add student](https://imgur.com/uHGHsdN.png)  

---

## 4. Technologies Used

- **Face Recognition**: FaceNet
- **Face Detection**: Google ML Kit, OpenCV (Haar Cascade)
- **Mobile Platform**: Android (Java)
- **Database**: Microsoft SQL Server
- **Data Export**: Apache POI (Excel), Gson (JSON)
- **Libraries**: OpenCV, JDBC

---

## 5. Demo

🎥 **Project Demo Video**  
Click the link below to watch the full demonstration of the FETEL Attendance system in action, including facial registration, real-time recognition, and attendance management:

[![Watch Demo on YouTube](https://img.youtube.com/vi/_pq62qO4UZc/0.jpg)](https://www.youtube.com/watch?v=_pq62qO4UZc)  
➡️ [Watch on YouTube](https://www.youtube.com/watch?v=_pq62qO4UZc)



# To install the PROJECT
First, You need create DATABASE
-----------------------------------------------------------------------
    DROP DATABASE PROJECT;
    CREATE DATABASE PROJECT;
    USE PROJECT;

    CREATE TABLE giangvien (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        username NCHAR(20) NOT NULL,
        password NCHAR(20) NOT NULL,
        email NCHAR(50) NOT NULL,
    );

    CREATE TABLE CLASS (
        id INT PRIMARY KEY IDENTITY(1,1),
        name_class NVARCHAR(255) NOT NULL,
        name_subject NVARCHAR(255) NOT NULL,
        background NVARCHAR(255) NOT NULL
    );
    
    CREATE TABLE THAMGIA (
	classid INT,
	code_student NVARCHAR(255),
	PRIMARY KEY (classid, code_student),
	FOREIGN KEY (classid) REFERENCES CLASS(id),
	FOREIGN KEY (code_student) REFERENCES STUDENT_LIST(code_student)
    );
   
    CREATE TABLE STUDENT_LIST (
        username NVARCHAR(255),
        password NVARCHAR(255),
        email NVARCHAR(255),
	name_student NVARCHAR(255) ,
	code_student NVARCHAR(255) PRIMARY KEY,
	date_of_birth  DATE,
	ImageData VARBINARY(MAX),
	FaceVector VARBINARY(MAX),
	Title NVARCHAR(255),
	Distance FLOAT,
	Id NVARCHAR(255),
    );
    CREATE TABLE ATTENDANCE (
    uniqueID INT IDENTITY(1,1) PRIMARY KEY,
    name_student NVARCHAR(255) ,
    code_student NVARCHAR(255),
    date_of_birth  DATE,
    attendance_date DATE,
    classId INT NOT NULL,
    FOREIGN KEY (classId) REFERENCES CLASS(id), 
    FOREIGN KEY (code_student) REFERENCES STUDENT_LIST(code_student)
    );
 Insert data into the CLASS table.
-----------------------------------------------------------------------
    INSERT INTO CLASS (name_class, name_subject, background) VALUES ('K20-Fetel', 'Lap trinh java', '1');
    INSERT INTO CLASS (name_class, name_subject, background) VALUES ('K20-Fetel', 'He thong nhung', '2');
    INSERT INTO CLASS (name_class, name_subject, background) VALUES ('K20-Fetel', 'Thiet ke Logic kha trinh', '3');
    INSERT INTO CLASS (name_class, name_subject, background) VALUES ('K20-Fetel', 'Ket noi va thu nhan du lieu trong IOT', '4');
    INSERT INTO CLASS (name_class, name_subject, background) VALUES ('K20-Fetel', 'Thiet ke SoC', '5');
    INSERT INTO CLASS (name_class, name_subject, background) VALUES ('K20-Fetel', 'TH lap trinh Java', '6');

 Insert data into the giangvien table.
-----------------------------------------------------------------------
    INSERT INTO giangvien (username, password, email) VALUES ('phuc', '123', 'thanhphuc104hd@gmail.com');
    INSERT INTO giangvien (username, password, email) VALUES ('khanh', '456', 'titanthophap2@gmail.com');

Then you need to adjust the IP of the network you are using as your local network, your SQLServer account username and password in the SQLConnection.java and SQLConnectionInBackGround.java files. 
-----------------------------------------------------------------------
      private static String ip = "yourip";
      private static String username = "yourusername";
      private static String password = "yourpassword";
#Note:
- The default admin account is: admin/123. You can only edit this account by editing the code in the Login section
- In some cases, the clone may fail. Downloading the ZIP may fix this.
- You may get a CMakeLists.txt to exist error. To fix this error you need to copy the openCV folder to the corresponding path.
