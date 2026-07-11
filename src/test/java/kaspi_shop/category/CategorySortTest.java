package kaspi_shop.category;

import kaspi_shop.base.BaseTest;
import kaspi_shop.constants.Locator;
import kaspi_shop.pages.CategoryPage;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class CategorySortTest extends BaseTest {
    protected CategoryPage categoryPage;

    @Test
    public void checkSortingPopular() throws InterruptedException {
        entryToListItem(Locator.POPULAR_LIST);
        By feedback = By.xpath("//a[@class='item__rating-link']//span");
        Assert.assertFalse(categoryPage.getElement(feedback).getText().isEmpty());
    }

    @Test
    public void checkSortingNew() throws InterruptedException {
        entryToListItem(Locator.NEW_LIST);

        By feedback = By.xpath("//a[@class='item__rating-link']");

        Assert.assertFalse(categoryPage.isDisplayed(feedback));

    }

    @Test
    public void checkSortingCheep() throws InterruptedException {
        entryToListItem(Locator.CHEEP_PRICE_LIST);

    }

    @Test
    public void checkSortingExpansive() throws InterruptedException {
        entryToListItem(Locator.EXPENSIVE_PRICE_LIST);

        Assert.assertTrue(string_to_price() > 500000);
    }

    @Test
    public void checkSortingTopRating() throws InterruptedException {
        entryToListItem(Locator.TOP_RATING_LIST);

        By feedback = By.xpath("//a[@class='item__rating-link']//span");

        String text = categoryPage.getText(feedback).replaceAll("[^0-9]", "");
        int digit = Integer.parseInt(text);

        Assert.assertTrue(digit > 10000);

    }

    public void entryToListItem(By locator) throws InterruptedException {
        categoryPage = homePage.categoryPageOpen();
        categoryPage.click(Locator.ACTUAL_SELECT_LIST);
        categoryPage.getElement(locator).click();
        clickFirstItemCardWithRetry(Locator.ITEM_CARD);
    }

    public void clickFirstItemCardWithRetry(By locator) throws InterruptedException {
        for (int attempt = 0; attempt <= 3; attempt++) {
            try {
                categoryPage.getVisibleAll(locator).get(0).click();
                return; // получилось - выходим
            } catch (StaleElementReferenceException e) {
                if (attempt == 3) {
                    throw e;
                }
                Thread.sleep(300);
            }
        }
    }

    public int string_to_price(){
        By price_link = By.cssSelector("[class=\"item__price-once\"]");
        String priceText = categoryPage.getText(price_link);
        String digits = priceText.replaceAll("[^0-9]", "");
        int price = Integer.parseInt(digits);
        return price;
    }


}
