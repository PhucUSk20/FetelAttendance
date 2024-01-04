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
