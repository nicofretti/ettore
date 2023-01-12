package it.ettore.e2e.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class CoursesPage extends PageObject{

    public CoursesPage(WebDriver driver) {
        super(driver);
    }
    @FindBy(id = "fullName")
    private WebElement fullName;

    @FindBy(xpath = "//a[@href='/logout']")
    private WebElement logout;

    @FindBy(xpath = "//a[@href='']")
    private WebElement returnToMyCourses;

    @FindBy(tagName = "button")
    private WebElement addNewCourse;

    @FindBy(id = "courseDetailsLink")
    private WebElement courseDetailsLink;

    @FindBy(id = "coursePeriod")
    private WebElement coursePeriod;

    @FindBy(id = "courseDescription")
    private WebElement courseDescription;

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    public String getFullName() {
        return fullName.getText();
    }

    public LoginPage clickLogout() {
        logout.click();
        return new LoginPage(driver);
    }

    public CoursesPage clickReturnToMyCourses() {
        returnToMyCourses.click();
        return new CoursesPage(driver);
    }

   /* public AddNewCoursePage clickAddNewCourse() {
        addNewCourse.click();
        return new AddNewCoursePage(driver);
    }*/

    public CourseDetailsPage clickCourseDetailsLink() {
        courseDetailsLink.click();
        return new CourseDetailsPage(driver);
    }

    public String getCoursePeriod() {
        return coursePeriod.getText();
    }

    public String getCourseDescription() {
        return courseDescription.getText();
    }
}
