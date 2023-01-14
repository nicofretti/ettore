package it.ettore.e2e.po;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ErrorsComponent extends PageObject {
    public ErrorsComponent(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".et-errors > .et-error")
    private List<WebElement> errors;

    public static class ErrorComponent {
        // We need a reference to the parent, so we can call refresh() to refresh the list of displayed errors after we
        // dismiss one
        private ErrorsComponent parent;
        private WebElement element;

        public ErrorComponent(ErrorsComponent parent, WebElement element) {
            this.parent = parent;
            this.element = element;
        }

        public String getMessage() {
            return element.getText();
        }

        public void dismiss() {
            element.click();
            this.parent.refresh();
        }
    }

    /**
     * Fetches the list of error components currently being displayed in the page. Useful if you want to interact with
     * them.
     */
    public List<ErrorComponent> getErrors() {
        return errors.stream().map(element -> new ErrorComponent(this, element)).collect(Collectors.toList());
    }

    /**
     * Fetches the list of error components currently being displayed in the page but then only returns the set of their
     * messages. Useful if only care about the messages (not interested in interacting) and the order in which they are
     * displayed doesn't matter.
     */
    public Set<String> getErrorMessageSet() {
        return getErrors().stream().map(ErrorComponent::getMessage).collect(Collectors.toSet());
    }
}
