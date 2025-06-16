# rakbank-student-management

HOW TO START THE MICROSERVICES
------------------------------

1. Start the student microservice first so that you can add student information.

   C:\Projects\student.management\student-service>mvn clean install
   
   C:\Projects\student.management\student-service>mvn spring-boot:run

Then you can use this microservice using either swagger or postman.

Swagger URL 
--------------
http://localhost:8080/api/swagger-ui/index.html

Postman URL
--------------
http://localhost:8080/api/students

Sample input for POST:

{
  "studentId": 12345,
  "studentName": "Marks Bhele",
  "grade": "12E",
  "mobileNumber": "0987654321",
  "schoolName": "Lutubeni High"
}

Get the outpuut using GET:

Sample input for getting  student information:

http://localhost:8080/api/students/987651


-----------------------------------------------------------------------------------------------------------------------------------------

2. Start the student fee microservice so that you can add student payment information.

   C:\Projects\student.management\fee-service>mvn clean install
   
   C:\Projects\student.management\fee-service>mvn spring-boot:run

Then you can use this microservice using either swagger or postman.

Swagger URL 
--------------
http://localhost:8081/api/swagger-ui/index.html#/

Postman URL
--------------
http://localhost:8081/api/fees/987651?amount=9000

Sample input for POST:

{
  "studentId": 987651,
  "amount": 9000
}

Get the outpuut using GET:

Sample input for getting student payment information:

http://localhost:8080/api/students/987651
