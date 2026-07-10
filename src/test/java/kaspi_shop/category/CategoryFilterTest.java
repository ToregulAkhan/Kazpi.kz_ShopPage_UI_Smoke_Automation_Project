package kaspi_shop.category;

import kaspi_shop.base.BaseTest;
import kaspi_shop.pages.CategoryPage;
import kaspi_shop.pages.HomePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CategoryFilterTest extends BaseTest {
    protected CategoryPage categoryPage;

    @Test
    public void checkCategoryOpened(){
        categoryPage = homePage.categoryPageOpen();
        By allCategory = By.xpath("//a[@href=\"/shop/c/categories/\" and @class=\"tree__link\"]");
        Assert.assertEquals(categoryPage.getElement(allCategory).getText(), "Все категории");
    }

    @Test
    public void checkFilters() throws InterruptedException {
        categoryPage = homePage.categoryPageOpen();

        By price_10000 = By.xpath("//span[text()='200 000 - 499 999 т']");
        By samsung = By.xpath("//span[text()='Samsung']");
        By sulpak = By.xpath("//span[text()='Sulpаk']");

        List<By> filterLink = List.of(price_10000, samsung, sulpak);
        By activeFilterRow = By.cssSelector(".filters__filter-row._active");

        int expectedCount = 0;

        for (By filter : filterLink) {
            categoryPage.click(filter);
            expectedCount++;   // после каждого клика ожидаем на 1 активный фильтр больше

            int attempts = 0;
            while (true) {
                try {
                    List<WebElement> activeFilters = categoryPage.getVisibleAll(activeFilterRow);
                    Assert.assertEquals(activeFilters.size(), expectedCount,
                            "Ожидали " + expectedCount + " активных фильтров");

                    System.out.println(activeFilters.get(activeFilters.size() - 1).getText());
                    break;   // проверка прошла - выходим из retry-цикла, переходим к следующему фильтру

                } catch (AssertionError e) {
                    attempts++;
                    if (attempts == 4) {
                        throw e;   // 4 попытки исчерпаны - тест реально падает
                    }
                }
            }
        }
    }
}
