package it.ettore.e2e.po.student;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import it.ettore.model.Course.Category;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class StudentSearchPage extends PageObject {
    public StudentSearchPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @EqualsAndHashCode
    public static class SearchResultComponent {
        private WebDriver driver;
        private WebElement element;

        public SearchResultComponent(WebDriver driver, WebElement element) {
            this.driver = driver;
            this.element = element;
        }

        public String getName() {
            return element.findElement(By.className("et-name")).getText();
        }

        public String getPeriod() {
            return element.findElement(By.className("et-period")).getText();
        }

        public String getDescription() {
            return element.findElement(By.className("et-description")).getText();
        }

        public boolean canJoin() {
            return element.findElement(By.tagName("button")).getAttribute("onclick") != null;
        }

        public StudentCoursesPage join() {
            element.findElement(By.tagName("button")).click();
            return new StudentCoursesPage(driver);
        }
    }

    @FindBy(css = ".et-content > div")
    private List<WebElement> searchResults;

    public List<StudentSearchPage.SearchResultComponent> getSearchResults() {
        return searchResults.stream().map(element -> new StudentSearchPage.SearchResultComponent(driver, element)).collect(Collectors.toList());
    }

    @FindBy(xpath = "//select[@name='category']")
    private WebElement selectCategory;
    @FindBy(xpath = "//input[@name='text']")
    private WebElement inputText;
    @FindBy(xpath = "//select[@name='startingYear']")
    private WebElement selectStartingYear;

    public void setCategory(Category category) {
        String value = "";
        if (category != null) {
            value = category.toString();
        }

        Select select = new Select(selectCategory);
        select.selectByValue(value);
    }

    public void setTextQuery(String query) {
        inputText.clear();
        inputText.sendKeys(query);
    }

    public void setStartingYear(Integer startingYear) {
        String value = "";
        if (startingYear != null) {
            value = startingYear.toString();
        }

        Select select = new Select(selectStartingYear);
        select.selectByValue(value);
    }

    @FindBy(id = "btn-search")
    private WebElement searchButton;

    public StudentSearchPage search() {
        searchButton.click();
        return new StudentSearchPage(driver);
    }

    public boolean isNoResultsErrorShown() {
        try {
            driver.findElement(By.id("et-no-results"));
            // No exception means the element is present
            return true;
        } catch (NoSuchElementException exc) {
            return false;
        }
    }
}
