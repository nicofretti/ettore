package it.ettore.e2e.po.professor.courses;
import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.PageObject;
import it.ettore.e2e.po.professor.ProfessorManagePage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorCoursePage extends PageObject {
    public ProfessorCoursePage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(className = "et-name")
    private WebElement name;

    @FindBy(className = "et-period")
    private WebElement period;

    @FindBy(className = "et-description")
    private WebElement description;

    @FindBy(id = "btn-edit")
    private WebElement editButton;

    @FindBy(id = "btn-goto-manage")
    private WebElement gotoManageButton;

    @FindBy(id = "btn-goto-lessons")
    private WebElement gotoLessonsButton;

    public String getName() {
        return name.getText();
    }

    public String getPeriod() {
        return period.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public ProfessorManagePage gotoManage() {
        gotoManageButton.click();
        return new ProfessorManagePage(driver);
    }

    public ProfessorLessonsPage goToLessons() {
        gotoLessonsButton.click();
        return new ProfessorLessonsPage(driver);
    }

}

