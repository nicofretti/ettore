package it.ettore.e2e.po;

import it.ettore.utils.Breadcrumb;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class Header extends PageObject {
    public Header(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "full-name")
    private WebElement fullName;

    @FindBy(linkText = "Logout")
    private WebElement logoutButton;

    @FindBy(css = ".et-breadcrumbs > a")
    private List<WebElement> breadcrumbs;

    public String getFullName() {
        return fullName.getText();
    }

    public LoginPage logout() {
        logoutButton.click();
        return new LoginPage(driver);
    }

    private String getURLPath(String url) {
        try {
            URL urlParsed = new URL(url);
            return urlParsed.getPath();
        } catch (MalformedURLException exc) {
            return "bad url";
        }
    }

    public List<Breadcrumb> getBreadcrumbs() {
        return breadcrumbs.stream().map(element -> new Breadcrumb(
                element.getText(),
                getURLPath(element.getAttribute("href"))
        )).collect(Collectors.toList());
    }
}
