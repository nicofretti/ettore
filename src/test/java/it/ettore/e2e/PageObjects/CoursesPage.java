package it.ettore.e2e.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class CoursesPage extends PageObject{

    public CoursesPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//head/title")
    private WebElement title;

    @FindBy(xpath = "//body/div[1]/p")
    private WebElement fullName;

    @FindBy(xpath = "//body/div[1]/a")
    private WebElement logout;

    @FindBy(xpath = "//body/div[2]/div/a")
    private WebElement returnToMyCourses;

    @FindBy(xpath = "//body/div[3]/button")
    private WebElement addNewCourse;

    @FindBy(xpath = "//body/div[3]/div/div/a")
    private WebElement courseDetailsLink;

    @FindBy(xpath = "//body/div[3]/div/div/p")
    private WebElement coursePeriod;

    @FindBy(xpath = "//body/div[3]/div/p")
    private WebElement courseDescription;

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return title.getText();
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
