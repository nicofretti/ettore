# Ettore

Ettore is a platform for e-learning where there are two types of users: professors and students.The professors can create courses where they have the possibilities to upload courses that are written in Markdown. On the other part students can ask to join the courses that are taught by the professor and then be able to look at the lessons available in that course.

## Technology Stack

The project is written in **Java 11** with the support of **Gradle** v6.3 as a build tool and Spring Boot as a framework.The database that we decided to use is **H2** because it is an in-memory database and it is very easy to use.The project follows a MVC pattern and it is divided in three main packages: controller, model and templates(which are our view components).On the front-end we use **TailwindCSS** as a CSS framework and Thymeleaf as a template engine. For the testing phase we used Selenium for end2end test Junit4 with "bonigarcia webdrivermanager". **Lombok** was also used to reduce the amount of boilerplate code like getter/setter...

## Development process

We initially did a meeting to discuss which of the proposed project to develop and we decided to go with the e-learning platform. Then we started writing the stories and further developed their most interesting subsections into more detailed scenarios as later explained below.Then before starting with the actual development we created a **Figma** board on which we sketched out a basic design of the project. This helped us to have a clear idea of how the project should look like. It also allowed us to better estimate to time required to develop each page in the project.This way it was easier to divide the work among the team members and to assign each member a Task.

For managing the tasks we created a **Trello** board where we divided the tasks and we assigned them to the members of the team. We also decided to use the **GitFlow** workflow for coordinating the development on the repository hosted on **GitHub**.We created a branch for each feature and we merged them into the develop branch when they were ready.During the development phase we used **Discord** to communicate with each other and to coordinate the work every time that we felt the need to do so.Each branch contains both the code and the tests that we wrote for that feature.For a branch to be actually merged into develop a Pull Request had to opened by the author of the branch and the other members of the team had to review the code and possibly modify or refactor before finally approving the request.

### CI/CD

To ensure the quality of our codebase at all times we made use of the free CI/CD platform provided by **Github** called **Github Actions**. This allowed us to run the tests automatically every time a new commit was pushed to the repository or a Pull Request was created.This way we could be sure that the code we were pushing was working and that it was not breaking the existing codebase, giving us the peace of mind that the code provided by each member of the team was working as expected.

## Authentication system

The spring boot authentication system was very complex to implement so we decide to create our own authentication system.
Upon logging in, the user's client is sent a pair of cookies containing his/her email and the other containing the hash of the password.These cookies are then sent along each following request.there is then an Interceptor that checks if the cookies are present and if they are valid. If they are not valid the user is redirected to the login page.The handler of the logout request simply deletes the cookies.

## Code Quality Assurance

To guarantee a codebase of high quality we used automated tests. We used **JUnit** for unit testing and **Selenium** for end2end testing.This allowed us to have a clear idea of the quality of our code and to improve it if necessary by periodically making improvements and refactoring the code to make it more readable and maintainable.

In total we have **130** test dived in two main categories:

- **Unit tests**: 58
  These are mainly used to test the logic of our models since there we have code that checks that some guarantees are always respected. For example, we have a test that checks that the a student can not be accpeted to join a course if he/she has never requested to join it.

- **End2end tests**: 72
  These are used to test the logic of our controllers and the interaction between the different components of the project and to check different parts of the code that were unreachable with unit tests.

## Unit tests

Here we have a list of the most immportant unit tests that we have implemented:

-
-

## End2end tests

Here we have a list of the most immportant end2end tests that we have implemented:

-
-

## Code Coverage
