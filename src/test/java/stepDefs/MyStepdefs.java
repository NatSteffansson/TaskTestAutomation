package stepDefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class MyStepdefs {
    WebDriver driver;
    private String lastName;
    private String password;
    private String confirmPassword;

    private void click(By by) {
        (new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.elementToBeClickable(by));
        driver.findElement(by).click();
    }

    private String getComplexRandomMail() {
        String lowerCharacters = "abcdefghijklmnopqrstuvwxyz";
        String upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numberCharacters = "0123456789";
        String symbolCharacters = "!?&%";
        return STR."\{RandomStringUtils.random(3, lowerCharacters)}\{RandomStringUtils.random(1, upperCharacters)}\{RandomStringUtils.random(1, numberCharacters)}\{RandomStringUtils.random(1, symbolCharacters)}@gmail.com";
    }

    @Given("I open the browser {string} with the registration page")
    public void iOpenTheBrowserWithTheRegistrationPage(String browserName) {
        if (browserName.equals("Edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            driver = new EdgeDriver(options);
        } else if (browserName.equals("Chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);
        } else {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--start-maximized");
            driver = new FirefoxDriver(options);
        }
        driver.get("https://membership.basketballengland.co.uk/NewSupporterAccount/");
    }

    @When("I fill out the compulsory fields {string}, {string}, {string}")
    public void iFillOutTheCompulsoryFields(String dateOfBirth, String firstName, String lastName) {
        this.lastName = lastName;
        driver.findElement(By.id("dp")).sendKeys(dateOfBirth);
        click(By.cssSelector("td.active.day"));
        driver.findElement(By.id("member_firstname")).sendKeys(firstName);
        driver.findElement(By.id("member_lastname")).sendKeys(lastName);
    }

    @And("I fill out the registration form with compulsory email and confirmed email")
    public void iFillOutTheRegistrationFormWithCompulsoryEmailAndConfirmedEmail() {
        String email = getComplexRandomMail();
        driver.findElement(By.cssSelector("#member_emailaddress")).sendKeys(email);
        driver.findElement(By.cssSelector("#member_confirmemailaddress")).sendKeys(email);
    }

    @And("I fill out the registration form with {string} and {string}")
    public void iFillOutTheRegistrationFormWithAnd(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
        driver.findElement(By.id("signupunlicenced_password")).sendKeys(password);
        driver.findElement(By.id("signupunlicenced_confirmpassword")).sendKeys(confirmPassword);
        if (!confirmPassword.equals(password)) {
            click(By.cssSelector("label[for='sign_up_25']")); //click(By.cssSelector("div.caption"));
        }
    }

    @And("I {string} the checkbox with terms and conditions")
    public void iTheCheckboxWithTermsAndConditions(String click) {
        Boolean isClicked = Boolean.parseBoolean(click);
        if (isClicked) {
            click(By.cssSelector("label[for='sign_up_25']"));
        }
    }

    @And("I click the checkbox with 18 over and terms of conduct")
    public void iClickTheCheckboxWith18OverAndTermsOfConduct() {
        click(By.cssSelector("label[for='sign_up_26']"));
        click(By.cssSelector("label[for='fanmembersignup_agreetocodeofethicsandconduct']"));
    }

    @And("I press the confirm and join button")
    public void iPressTheConfirmAndJoinButton() {
        click(By.name("join"));
    }

    @Then("I should get the outcome {string}")
    public void iShouldGetTheOutcome(String outcome) {
        if (outcome.equals("registered")) {
            String expected = "Your Basketball England Membership Number is:";
            String actual = driver.findElement(By.cssSelector("h5.bold")).getText();
            assertEquals(expected, actual);
        } else if (outcome.equals("warning")) {
            if (lastName.isEmpty()) {
                String expected = "Last Name is required";
                String actual = driver.findElement(By.cssSelector("span[for='member_lastname']")).getText();
                assertEquals(expected, actual);
            } else if (!confirmPassword.equals(password)) {
                String expected = "Password did not match";
                String actual = driver.findElement(By.cssSelector("span[for='signupunlicenced_confirmpassword']")).getText();
                assertEquals(expected, actual);
            } else {
                String expected = "You must confirm that you have read and accepted our Terms and Conditions";
                String actual = driver.findElement(By.cssSelector("span[for='TermsAccept']")).getText();
                assertEquals(expected, actual);
            }
        }
        driver.quit();
    }
}
