package it.ettore.e2e.student;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.student.StudentCoursesPage;
import it.ettore.e2e.po.student.StudentSearchPage;
import it.ettore.model.*;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static it.ettore.TestUtil.assertEmpty;
import static org.junit.Assert.*;

public class StudentSearch extends E2EBaseTest {
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private CourseRepository repoCourse;
    @Autowired
    private LessonRepository repoLesson;

    User student;

    // Create a bunch of courses, each with a unique property, and use them in all test cases for the search page

    /**
     * A course that the test student has neither joined nor requested to
     **/
    Course freshCourse;
    /**
     * A course that the test student has requested to join
     **/
    Course courseRequested;
    /**
     * A course that the test student has joined
     **/
    Course courseJoined;

    /**
     * The only course in the history category
     **/
    Course courseHistory;
    /**
     * The only course with "Leibniz" in its name
     **/
    Course courseNameLeibniz;
    /**
     * The only course with "Fourier" in its name, but it's all in uppercase
     **/
    Course courseNameUppercaseFourier;
    /**
     * The only course with "Fourier" in its description
     **/
    Course courseDescriptionNewton;
    /**
     * The only course in the 2021/2022 period
     **/
    Course course2021;

    @Before
    public void prepareSearchTest() {
        // Remove any course that might have been inserted by the DB bootstrapper
        repoCourse.findAll().forEach(course -> {
            repoLesson.deleteAll(repoLesson.findByCourse(course));
            repoCourse.delete(course);
        });

        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePasswordProfessor", User.Role.PROFESSOR);
        repoUser.save(professor);

        String email = "some.student@ettore.it";
        String password = "SomeSecurePasswordStudent";
        student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        freshCourse = new Course("Not joined nor requested", "Course description", 2023, Course.Category.Maths, professor);

        courseRequested = new Course("Requested", "Course description", 2023, Course.Category.Maths, professor);
        courseRequested.requestJoin(student);

        courseJoined = new Course("Joined", "Course description", 2023, Course.Category.Maths, professor);
        courseJoined.requestJoin(student);
        courseJoined.acceptStudent(student);

        courseHistory = new Course("Course name that's not about maths", "Course description", 2023, Course.Category.History, professor);
        courseNameLeibniz = new Course("Course about Leibniz and his discoveries", "Course description", 2023, Course.Category.Maths, professor);
        courseNameUppercaseFourier = new Course("Course about FOURIER transform", "Course description", 2023, Course.Category.Maths, professor);
        courseDescriptionNewton = new Course("Course name with an interesting description", "Course description and it has Newton in it", 2023, Course.Category.Maths, professor);
        course2021 = new Course("Course name not in the usual year", "Course description", 2021, Course.Category.Maths, professor);

        repoCourse.saveAll(List.of(freshCourse, courseRequested, courseJoined, courseHistory, courseNameLeibniz, courseNameUppercaseFourier, courseDescriptionNewton, course2021));

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        coursesPage.searchCourse();
    }

    /**
     * Check that the preparation step for all tests related to the search page leaves us at /student/courses/search
     */
    @Test
    public void atTheCorrectURLAfterPreparation() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        // Should be in the search page
        assertEquals(String.format("/student/courses/search"), currentPath());
    }

    /**
     * Check that search displays some courses by default. Also checks that the "No courses found" message is hidden
     * when there are some courses to show.
     */
    @Test
    public void displaysSomeCourses() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        assertTrue(searchPage.getSearchResults().size() > 0);
        // Assert that the "No courses found" message is hidden when there are search results
        assertFalse(searchPage.isNoResultsErrorShown());
    }

    /**
     * Check that the course that we haven't joined nor requested to is shown
     */
    @Test
    public void displaysNewCourse() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        assertTrue(searchPage.getSearchResults().stream().anyMatch(someCourse -> {
            return someCourse.getName().equals(freshCourse.getName());
        }));
    }

    /**
     * Check that the course that we haven't joined nor requested to can be joined by pressing the button
     */
    @Test
    public void canJoinNewCourse() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        StudentSearchPage.SearchResultComponent targetCourse = searchPage
                .getSearchResults()
                .stream()
                .filter(someCourse -> someCourse.getName().equals(freshCourse.getName()))
                .findFirst()
                .orElseThrow();
        assertTrue(targetCourse.canJoin());
    }

    /**
     * Check that the course that we have requested to join is shown anyways
     */
    @Test
    public void displaysRequestedCourse() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        assertTrue(searchPage.getSearchResults().stream().anyMatch(someCourse -> {
            return someCourse.getName().equals(courseRequested.getName());
        }));
    }

    /**
     * Check that we can't press the button to join a course that we have already requested to join
     */
    @Test
    public void cantJoinRequestedCourse() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        StudentSearchPage.SearchResultComponent targetCourse = searchPage
                .getSearchResults()
                .stream()
                .filter(someCourse -> someCourse.getName().equals(courseRequested.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(targetCourse.canJoin());
    }

    /**
     * Check that the course that we haven't yet requested to join can be requested by pressing the button. We're then
     * brought back to the courses list page
     */
    @Test
    public void requestCourseJoin() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        StudentSearchPage.SearchResultComponent targetCourse = searchPage
                .getSearchResults()
                .stream()
                .filter(someCourse -> someCourse.getName().equals(freshCourse.getName()))
                .findFirst()
                .orElseThrow();
        StudentCoursesPage coursesPage = targetCourse.join();
        assertEquals(String.format("/student/courses"), currentPath());

        // Get the lists of students that are requesting to join this course and assert that our student is there
        List<User> usersRequestingToJoin = new ArrayList<>();
        repoUser.findByCoursesRequesting(freshCourse).forEach(usersRequestingToJoin::add);

        assertTrue(usersRequestingToJoin.stream().anyMatch(someStudent -> someStudent.getId() == student.getId()));
    }

    /**
     * Check that the course that we have joined already is shown anyways
     */
    @Test
    public void displaysJoinedCourse() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        assertTrue(searchPage.getSearchResults().stream().anyMatch(someCourse -> {
            return someCourse.getName().equals(courseJoined.getName());
        }));
    }

    /**
     * Check that the course we can't press the button to join a course that we have already joined (i.e. accepted!)
     */
    @Test
    public void cantJoinJoinedCourse() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        StudentSearchPage.SearchResultComponent targetCourse = searchPage
                .getSearchResults()
                .stream()
                .filter(someCourse -> someCourse.getName().equals(courseJoined.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(targetCourse.canJoin());
    }

    /**
     * Check that search returns no results when there is no course that matches the given query. Also check that, in
     * such case, a proper message is shown
     */
    @Test
    public void crazyQuery() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setTextQuery("Some totally crazy query that will definitely produce no results");
        searchPage = searchPage.search();
        assertEmpty(searchPage.getSearchResults());
        assertTrue(searchPage.isNoResultsErrorShown());
    }

    /**
     * Checks that the search displays courses matching the given category, and hides the others
     */
    @Test
    public void findCourseByCategory() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setCategory(Course.Category.History);
        searchPage = searchPage.search();
        // There should be just one course in the history category. If this assert goes well, then the other courses
        // have been hidden properly because we did create some courses with a different category in the preparation step
        assertEquals(1, searchPage.getSearchResults().size());
        // Check that it's exactly the course that we want
        assertEquals(courseHistory.getName(), searchPage.getSearchResults().get(0).getName());
    }

    /**
     * Checks that the search really displays courses with different category when the query uses category="Any category"
     */
    @Test
    public void findAnyCategory() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setCategory(null);
        searchPage = searchPage.search();
        // We know that courseNameLeibniz is Maths and courseHistory is History (hence, different categories). So simply
        // check that we can find both when using an "Any category" query
        assertTrue(searchPage.getSearchResults().stream().anyMatch(someCourse -> someCourse.getName().equals(courseNameLeibniz.getName())));
        assertTrue(searchPage.getSearchResults().stream().anyMatch(someCourse -> someCourse.getName().equals(courseHistory.getName())));
    }

    /**
     * Checks that the search displays courses whose name contain the given query string, and hides the others. For this
     * specific testcase we're providing the precise substring contained in the course's name: no uppercase-lowercase
     * difference
     */
    @Test
    public void findCourseByName() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setTextQuery("Leibniz");
        searchPage = searchPage.search();
        // There should be just one course with "Leibniz" in the name. If this assert goes well, then the other courses
        // have been hidden properly because we did create some courses whose name didn't contain "Leibniz"
        assertEquals(1, searchPage.getSearchResults().size());
        // Check that it's exactly the course that we want
        assertEquals(courseNameLeibniz.getName(), searchPage.getSearchResults().get(0).getName());
    }

    /**
     * Checks that the search displays courses whose name contain the given query string ignoring the precise case. All
     * other courses should be hidden
     */
    @Test
    public void findCourseByNameIgnoreCase() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setTextQuery("foUrIEr");
        searchPage = searchPage.search();
        // There should be just one course with "foUrIEr" in the name (actually, the existing course contains "Fourier"
        // so in this testcase we're checking that uppercase-lowercase doesn't make ad difference). If this assert goes
        // well, then the other courses have been hidden properly because we did create some courses whose name didn't
        // contain "foUrIEr"
        assertEquals(1, searchPage.getSearchResults().size());
        // Check that it's exactly the course that we want
        assertEquals(courseNameUppercaseFourier.getName(), searchPage.getSearchResults().get(0).getName());
    }

    /**
     * Checks that the search displays courses whose description contain the given query string, and hides the others
     */
    @Test
    public void findCourseByDescription() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setTextQuery("Newton");
        searchPage = searchPage.search();
        // There should be just one course with "Newton" in the description. If this assert goes well, then the other
        // courses have been hidden properly because we did create some courses whose description didn't contain "Newton"
        assertEquals(1, searchPage.getSearchResults().size());
        // Check that it's exactly the course that we want
        assertEquals(courseDescriptionNewton.getName(), searchPage.getSearchResults().get(0).getName());
    }

    /**
     * Checks that the search displays courses matching the given starting year, and hides the others
     */
    @Test
    public void findCourseByStartingYear() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setStartingYear(2021);
        searchPage = searchPage.search();
        // There should be just one course in the 2021/2022 period. If this assert goes well, then the other courses
        // have been hidden properly because we did create some courses with a different starting year in the
        // preparation step
        assertEquals(1, searchPage.getSearchResults().size());
        // Check that it's exactly the course that we want
        assertEquals(course2021.getName(), searchPage.getSearchResults().get(0).getName());
    }

    /**
     * Checks that the search really displays courses with different starting years when the query uses
     * startingYear="Any year"
     */
    @Test
    public void findAnyYear() {
        StudentSearchPage searchPage = new StudentSearchPage(driver);
        searchPage.setStartingYear(null);
        searchPage = searchPage.search();
        // We expect to find at least two courses with different periods (a function of the startingYear) because they
        // exist in the database (we created them in the preparation step)

        // Get any period from any course
        String somePeriod = searchPage.getSearchResults().get(0).getPeriod();
        // And check that there is a course in the search results with a different period
        assertTrue(searchPage.getSearchResults().stream().skip(1).anyMatch(someCourse -> {
            return !someCourse.getPeriod().equals(somePeriod);
        }));
    }
}
