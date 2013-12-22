package org.robotframework.webdriverlibrary;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeywordOverload;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.apache.commons.io.FileUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;


@RobotKeywords
public class WebDriverKeywords {

    private static WebDriver drv;
    private int elementTimeout = 30;
    private int waitAfterAction = 0;

    public WebDriverKeywords() {
    }

    @RobotKeyword("Sets a timeout value, which is used for waiting elements to become visible on the page. "
                   + "Very useful when navigating across different pages and waiting for some timed event to occur. "
                   + "Suggested usage would reset this value before each test case. Default internal value is currently 30 seconds. "
                   + "The argument is given in seconds, that is, the argument 60 waits a minute before giving a timeout.\n\n"
                   + "Example:\n"
                   + "| SetFindElementTimeout | 30 |\n")
    @ArgumentNames({"timeout"})
    public void setFindElementTimeout(final String timeout) {
        this.elementTimeout = Integer.parseInt(timeout);
    }

    @RobotKeyword("Sets a waiting time, which is used to slow down the keyword execution. "
                   + "When given a value 3 as argument, each action is executed in three second intervals.\n\n"
                   + "Example:\n"
                   + "| SetWaitAfterAction | 5 |\n")
    @ArgumentNames({"wait"})
    public void setWaitAfterAction(final String wait) {
        this.waitAfterAction = Integer.parseInt(wait)*1000;
    }

    @RobotKeyword("Opens the sepcified browser. Supports firefox, chrome and ie.\n\n"
                   + "Examples:\n"
                   + "| OpenBrowser | firefox |\n"
                   + "| OpenBrowser | chrome  |\n"
                   + "| OpenBrowser | ie      |\n")
    @ArgumentNames({"browser"})
    public void openBrowser(final String browser) throws Exception {
        if (browser.equals("ie")) {
            drv = new InternetExplorerDriver();
        } else if (browser.equals("chrome")) {
            drv = new ChromeDriver();
        } else {
            FirefoxProfile prfl = new FirefoxProfile();
            prfl.setEnableNativeEvents(true);
            drv = new FirefoxDriver(prfl);
        }
        drv.get("about:blank");
    }

    @RobotKeyword("Closes all browsers started by the OpenBrowser keyword.\n\n"
                   + "Example:\n"
                   + "| CloseBrowser |\n")
    public void closeBrowser() {
        drv.quit();
    }

    @RobotKeyword("Navigates to the URL given as argument.\n\n"
                   + "Example:\n"
                   + "| NavigateToURL | http://www.google.com |\n")
    @ArgumentNames({"url"})
    public void navigateToURL(final String url) {
        drv.get(url);
    }

    @RobotKeyword("Deletes all cookies and in many cases clears all previous browsing data. "
                   + "Can be used in the beginning of each test case to start from a clean init-state.\n\n"
                   + "Examples:\n"
                   + "| ClearBrowserData |\n")
    public void clearBrowserData() {
        drv.manage().deleteAllCookies();
    }

    @RobotKeyword("Sets the size of the current browser window. "
                   + "First argument is width and the second argument is height.\n\n"
                   + "Example:\n"
                   + "| SetBrowserWindowSize | 1000 | 800 |\n")
    @ArgumentNames({"width","height"})
    public void setBrowserWindowSize(final String w, final String h) {
        drv.manage().window().setSize(new Dimension(Integer.parseInt(w), Integer.parseInt(h)));
    }

    @RobotKeyword("Gets the top left pixel coordinates of the current browser window.\n\n"
                   + "Example:\n"
                   + "| getBrowserWindowLocation |\n")
    public String getBrowserWindowLocation(final String w, final String h) {
        return drv.manage().window().getPosition().toString();
    }

    @RobotKeyword("Returns the title of the current web page.\n\n"
                   + "Example:\n"
                   + "| ${pageTitle}= | GetPageTitle |\n")
    public String getPageTitle() throws Exception {
        Thread.sleep(this.waitAfterAction);
        return drv.getTitle();
    }

    @RobotKeyword("Saves a screenshot of the current web page. "
                   + "The screenshot files are saved in a folder './scrshots' in a date-format MMddHHmmss.png.\n\n"
                   + "Example:\n"
                   + "| GetPageScreenshot |\n")
    public void getPageScreenshot() throws Exception {
        File scrFile = ((TakesScreenshot)drv).getScreenshotAs(OutputType.FILE);
        String fileName = new SimpleDateFormat("MMddHHmmss'.png'").format(new Date());
        FileUtils.copyFile(scrFile, new File("./scrshots/"+fileName)); 
    }

    @RobotKeyword("Executes the javascript snippet given as argument. "
                   + "Returns the text retuned by the javascript command. "
                   + "The format of the javascript command should be: return window.document.title;.\n\n"
                   + "Examples:\n"
                   + "| ${documentTitle}=   | ExecuteJavascript | return window.document.title;                             |\n"
                   + "| ${elementDisabled}= | ExecuteJavascript | return window.document.getElementById('someId').disabled; |\n"
                   + "| ${contentType}=     | ExecuteJavascript | return window.document.contentType;                       |\n")
    @ArgumentNames({"jScript"})
    public String executeJavascript(final String jScript) throws Exception {
        Thread.sleep(this.waitAfterAction);
        return (String)((JavascriptExecutor) drv).executeScript(jScript);
    }

    @RobotKeyword("Does a left mouse button click on the indicated web element given as argument. "
                   + "The keyword uses the specified elementTimeout to wait until the element is clickable. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ClickElement | id       | someId                  |\n"
                   + "| ClickElement | xpath    | //element[@id='someId'] |\n"
                   + "| ClickElement | linkText | myLinkText              |\n")
    @ArgumentNames({"by","id"})
    public void clickElement(final String by, final String id) throws Exception {
        getE(ExpectedConditions.elementToBeClickable(getBy(by,id))).click();
    }

    @RobotKeyword("Writes given text on the indicated text field element given as argument. "
                   + "The field is first cleared from previous text and then the new text is typed. "
                   + "The keyword uses the specified elementTimeout to wait until the field is visible. "
                   + "The field element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| WriteTextToField | id    | someFieldId             | givenText |\n"
                   + "| WriteTextToField | xpath | //element[@id='someId'] | givenText |\n"
                   + "| WriteTextToField | name  | myTextField             | givenText |\n")
    @ArgumentNames({"by","id","text"})
    public void writeTextToField(final String by, final String id, final String text) throws Exception {
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        e.clear();
        e.sendKeys(text);
    }

    @RobotKeyword("Selects given item on the indicated dropdown element given as argument. "
                   + "The selection method is based on the text of the dropdown item. "
                   + "The keyword uses the specified elementTimeout to wait until the dropdown is visible. "
                   + "The dropdown can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| SelectDropdownItem | id    | someId                  | itemText |\n"
                   + "| SelectDropdownItem | xpath | //element[@id='someId'] | itemText |\n"
                   + "| SelectDropdownItem | name  | myDropdown              | itemText |\n")
    @ArgumentNames({"by","id","text"})
    public void selectDropdownItem(final String by, final String id, final String text) throws Exception {
        new Select(getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)))).selectByVisibleText(text);
    }

    @RobotKeyword("Clicks the checkbox given as argument, if the checkbox is not already checked. "
                   + "The keyword uses the specified elementTimeout to wait until the dropdown is visible. "
                   + "The checkbox can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| SelectCheckbox | id    | someId                  |\n"
                   + "| SelectCheckbox | xpath | //element[@id='someId'] |\n"
                   + "| SelectCheckbox | name  | myCheckbox              |\n")
    @ArgumentNames({"by","id"})
    public void selectCheckbox(final String by, final String id) throws Exception {
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        if ( !e.isSelected() ) { e.click(); }
    }

    @RobotKeyword("Clicks the checkbox given as argument, if the checkbox is already checked. "
                   + "The keyword uses the specified elementTimeout to wait until the dropdown is visible. "
                   + "The checkbox can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| UnselectCheckbox | id    | someId                  |\n"
                   + "| UnselectCheckbox | xpath | //element[@id='someId'] |\n"
                   + "| UnselectCheckbox | name  | myCheckbox              |\n")
    @ArgumentNames({"by","id"})
    public void unselectCheckbox(final String by, final String id) throws Exception {
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        if ( e.isSelected() ) { e.click(); }
    }

    @RobotKeyword("Returns the text enclosed by the given web element. "
                   + "All the text in the decendant elements is also returned and the text is concatenated by a space. "
                   + "The keyword uses the specified elementTimeout to wait until the element is present in DOM. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ${text}= | GetTextOfElement | id    | someId                  |\n"
                   + "| ${text}= | GetTextOfElement | xpath | //element[@id='someId'] |\n"
                   + "| ${text}= | GetTextOfElement | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public String getTextOfElement(final String by, final String id) throws Exception {
        return getE(ExpectedConditions.presenceOfElementLocated(getBy(by,id))).getText().replace("\n"," ");
    }

    @RobotKeyword("Verifies that given element exists and is visible in DOM. "
                   + "The keyword uses the specified elementTimeout to wait until the element is present in DOM. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ElementExists | id    | someId                  |\n"
                   + "| ElementExists | xpath | //element[@id='someId'] |\n"
                   + "| ElementExists | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void elementExists(final String by, final String id) throws Exception {
        getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
    }

    @RobotKeyword("Verifies that given element does not exist in DOM. "
                   + "The keyword uses the specified elementTimeout to wait until the element disappears from DOM. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ElementDoesNotExist | id    | someId                  |\n"
                   + "| ElementDoesNotExist | xpath | //element[@id='someId'] |\n"
                   + "| ElementDoesNotExist | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void elementDoesNotExist(final String by, final String id) throws Exception {
        getB(ExpectedConditions.invisibilityOfElementLocated(getBy(by,id)));
    }

    @RobotKeyword("Verifies that given element exists and is visible in DOM and it contains expected text. "
                   + "The keyword uses the specified elementTimeout to wait until the element is present in DOM and contains the expected text. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ElementTextContains | id    | someId                  | text |\n"
                   + "| ElementTextContains | xpath | //element[@id='someId'] | text |\n"
                   + "| ElementTextContains | name  | myElement               | text |\n")
    @ArgumentNames({"by","id","text"})
    public void elementTextContains(final String by, final String id, final String text) throws Exception {
        getB(textInElement(getBy(by,id), text, true));
    }

    @RobotKeyword("Verifies that given element exists and is visible in DOM and it has expected text. "
                   + "The keyword uses the specified elementTimeout to wait until the element is present in DOM and has the expected text. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ElementTextEquals | id    | someId                  | text |\n"
                   + "| ElementTextEquals | xpath | //element[@id='someId'] | text |\n"
                   + "| ElementTextEquals | name  | myElement               | text |\n")
    @ArgumentNames({"by","id","text"})
    public void elementTextEquals(final String by, final String id, final String text) throws Exception {
        getB(textInElement(getBy(by,id), text, false));
    }

    @RobotKeyword("Verifies that given element exists and is visible in DOM and it does not contain expected text. "
                   + "The keyword uses the specified elementTimeout to wait until the containing text in given element disappears. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ElementTextNotContains | id    | someId                  | text |\n"
                   + "| ElementTextNotContains | xpath | //element[@id='someId'] | text |\n"
                   + "| ElementTextNotContains | name  | myElement               | text |\n")
    @ArgumentNames({"by","id","text"})
    public void elementTextNotContains(final String by, final String id, final String text) throws Exception {
        getB(ExpectedConditions.not(textInElement(getBy(by,id), text, true)));
    }

    @RobotKeyword("Verifies that given element exists and is visible in DOM and it does not contain expected text. "
                   + "The keyword uses the specified elementTimeout to wait until the text in given element disappears. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ElementTextNotEquals | id    | someId                  | text |\n"
                   + "| ElementTextNotEquals | xpath | //element[@id='someId'] | text |\n"
                   + "| ElementTextNotEquals | name  | myElement               | text |\n")
    @ArgumentNames({"by","id","text"})
    public void elementTextNotEquals(final String by, final String id, final String text) throws Exception {
        getB(ExpectedConditions.not(textInElement(getBy(by,id), text, false)));
    }

    @RobotKeyword("Clicks OK button on a javascript alert window. "
                   + "The keyword uses the specified elementTimeout to wait until the alert window appears.\n\n"
                   + "Example:\n"
                   + "| ClickPopupOk |\n")
    public void clickPopupOk() throws Exception {
        getA(ExpectedConditions.alertIsPresent()).accept();
    }

    @RobotKeyword("Clicks Cancel button on a javascript alert window. "
                   + "The keyword uses the specified elementTimeout to wait until the alert window appears.\n\n"
                   + "Example:\n"
                   + "| ClickPopupCancel |\n")
    public void clickPopupCancel() throws Exception {
        getA(ExpectedConditions.alertIsPresent()).dismiss();
    }

    @RobotKeyword("Switches the current context to the specified iFrame. "
                   + "The keyword uses the specified elementTimeout to wait until the frame appears.\n\n"
                   + "Example:\n"
                   + "| SwitchToFrame | someID |\n")
    @ArgumentNames({"id"})
    public void switchToFrame(final String id) throws Exception {
        drv = getD(ExpectedConditions.frameToBeAvailableAndSwitchToIt(id));
    }

    @RobotKeyword("Switches the current context out from an iFrame.\n\n"
                   + "Example:\n"
                   + "| SwitchToDefaultContext |\n")
    public void switchToDefaultContext() throws Exception {
        drv.switchTo().defaultContent();
    }

    @RobotKeyword("Switches the current context to other window, which can be referenced by giving a part of its title text as an argument. "
                   + "The keyword uses the specified elementTimeout to wait until the window is available.\n\n"
                   + "Example:\n"
                   + "| SwitchToWindowWithTitle | partial title text |\n")
    @ArgumentNames({"title"})
    public void switchToWindowWithTitle(final String title) throws Exception {
        drv = getD(switchToBrowserWindow("return window.document.title;", title, false));
    }

    @RobotKeyword("Switches the current context to other window, which can be referenced by giving its URL text as an argument. "
                   + "The keyword uses the specified elementTimeout to wait until the window is available.\n\n"
                   + "Example:\n"
                   + "| SwitchToWindowWithURL | URL text |\n")
    @ArgumentNames({"url"})
    public void switchToWindowWithURL(final String url) throws Exception {
        drv = getD(switchToBrowserWindow("return window.document.URL;", url, false));
    }

    @RobotKeyword("Switches the current context to next window, the 'next' is defined internally. "
                   + "The keyword uses the specified elementTimeout to wait until the window is available.\n\n"
                   + "Example:\n"
                   + "| SwitchToNextWindow |\n")
    public void switchToNextWindow() throws Exception {
        drv = getD(switchToBrowserWindow("window handle", "next index", true));
    }

    @RobotKeyword("Simulates a drag and drop from element1 to element2. "
                   + "The keyword uses the specified elementTimeout to wait until the elements are visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The elements can be located by using their DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| DragAndDrop | id    | someId                  | id    | someId                  |\n"
                   + "| DragAndDrop | xpath | //element[@id='someId'] | xpath | //element[@id='someId'] |\n"
                   + "| DragAndDrop | name  | myElement               | name  | myElement               |\n")
    @ArgumentNames({"by1","id1","by2","id2"})
    public void dragAndDrop(String by1, String id1, String by2, String id2) throws Exception {
        WebElement e1 = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by1,id1)));
        WebElement e2 = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by2,id2)));
        new Actions(drv).dragAndDrop(e1, e2).perform();
    }

    @RobotKeyword("Simulates a drag and drop measured by pixel-coordinate distance from given element. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| dragAndDropBy | id    | someId                  | 200 | 100 |\n"
                   + "| dragAndDropBy | xpath | //element[@id='someId'] | 300 | 0   |\n"
                   + "| dragAndDropBy | name  | myElement               | 10  | 10  |\n")
    @ArgumentNames({"by","id","xx","yy"})
    public void dragAndDropBy(String by, String id, String xx, String yy) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).dragAndDropBy(e, Integer.parseInt(xx), Integer.parseInt(yy)).perform();
    }

    @RobotKeyword("Just like a click, but does not release the left mouse button. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| MouseDownOnElement | id    | someId                  |\n"
                   + "| MouseDownOnElement | xpath | //element[@id='someId'] |\n"
                   + "| MouseDownOnElement | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void mouseDownOnElement(final String by, final String id) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).clickAndHold(e).perform();
    }

    @RobotKeyword("Releases the left mouse button after mousedown has been used. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| MouseUpOnElement | id    | someId                  |\n"
                   + "| MouseUpOnElement | xpath | //element[@id='someId'] |\n"
                   + "| MouseUpOnElement | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void mouseUpOnElement(final String by, final String id) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).release(e).perform();
    }

    @RobotKeyword("Simulates hovering a mouse over a web element. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| HoverOnElement | id    | someId                  |\n"
                   + "| HoverOnElement | xpath | //element[@id='someId'] |\n"
                   + "| HoverOnElement | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void hoverOnElement(final String by, final String id) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).moveToElement(e).perform();
    }

    @RobotKeyword("Double clicks the left mouse mouse button over a web element. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| DoubleClickOnElement | id    | someId                  |\n"
                   + "| DoubleClickOnElement | xpath | //element[@id='someId'] |\n"
                   + "| DoubleClickOnElement | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void doubleClickOnElement(final String by, final String id) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).doubleClick(e).perform();
    }

    @RobotKeyword("Clicks the right mouse mouse button over a web element. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| RightClickOnElement | id    | someId                  |\n"
                   + "| RightClickOnElement | xpath | //element[@id='someId'] |\n"
                   + "| RightClickOnElement | name  | myElement               |\n")
    @ArgumentNames({"by","id"})
    public void rightClickOnElement(final String by, final String id) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).contextClick(e).perform();
    }

    @RobotKeyword("Clicks the left mouse button at a pixel-coordinate distance from given element. "
                   + "The keyword uses the specified elementTimeout to wait until the element is visible. "
                   + "NOTE! Uses the webdriver's ActionBuilder class, which might not work on all newest browsers. "
                   + "Safest choise is to use older versions of Firefox and latest versions of Chrome. "
                   + "The element can be located by using its DOM definition via the selectors: "
                   + "id, name, xpath, className, linkText, partialLinkText, tagName or cssSelector.\n\n"
                   + "Examples:\n"
                   + "| ClickOffsetOnElement | id    | someId                  | 200 | 100 |\n"
                   + "| ClickOffsetOnElement | xpath | //element[@id='someId'] | 300 | 0   |\n"
                   + "| ClickOffsetOnElement | name  | myElement               | 10  | 10  |\n")
    @ArgumentNames({"by","id","xx","yy"})
    public void clickOffsetOnElement(String by, String id, String xx, String yy) throws Exception { 
        WebElement e = getE(ExpectedConditions.visibilityOfElementLocated(getBy(by,id)));
        new Actions(drv).moveToElement(e).moveByOffset(Integer.parseInt(xx),Integer.parseInt(yy)).click().perform();
    }


    // *** PRIVATE SUPPORT METHODS ***

    // return Alert object after dynamic wait
    private Alert getA(ExpectedCondition<Alert> condition) throws Exception {
        return new WebDriverWait(drv, this.elementTimeout).until(condition);
    }

    // return Boolean value after dynamic wait
    private Boolean getB(ExpectedCondition<Boolean> condition) throws Exception {
        return new WebDriverWait(drv, this.elementTimeout).until(condition);
    }
    
    // return WebDriver object after dynamic wait
    private WebDriver getD(ExpectedCondition<WebDriver> condition) throws Exception {
        return new WebDriverWait(drv, this.elementTimeout).until(condition);
    }

    // return WebElement object after dynamic wait
    private WebElement getE(ExpectedCondition<WebElement> condition) throws Exception {
        return new WebDriverWait(drv, this.elementTimeout).until(condition);
    }

    // return By object using reflection to the static By class
    // id, name, xpath, className, linkText, partialLinkText, tagName, cssSelector
    private By getBy(final String by, final String id) throws Exception {
        Thread.sleep(this.waitAfterAction);
        return (By) By.class.getMethod(by, String.class).invoke(null,id);
    }

    private static ExpectedCondition<Boolean> textInElement(final By locator, final String text, final Boolean contains) {
        return new ExpectedCondition<Boolean>() {
            String elementText;
            @Override
            public Boolean apply(WebDriver d) {
                try {
                    elementText = d.findElement(locator).getText().replace("\n", " ");
                    if (contains) return elementText.contains(text);
                    else          return elementText.equals(text);
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("text ('%s') to be present in element found by %s, which had text ('%s')", text, locator, elementText);
            }
        };
    }

    private static ExpectedCondition<WebDriver> switchToBrowserWindow(final String jScript, final String text, final Boolean next) {
        return new ExpectedCondition<WebDriver>() {
            @Override
            public WebDriver apply(WebDriver d) {
                    String wText = "";
                    String origHandle = d.getWindowHandle();
                    for (String handle : d.getWindowHandles()) {
                        if (next && !handle.equals(origHandle)) {
                            return d.switchTo().window(handle);
                        }
                        if (!next) {
                            d.switchTo().window(handle);
                            wText = (String)((JavascriptExecutor) d).executeScript(jScript);
                            if (wText.contains(text)) {
                                return d.switchTo().window(handle);
                            }
                        }
                    }
                    d.switchTo().window(origHandle);
                    return null;
            }

            @Override
            public String toString() {
                return String.format("window containing ('%s') to be present found by %s", text, jScript);
            }
        };
    }

} // End Of WebDriverKeywords
