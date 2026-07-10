package kaspi_shop.category;

import kaspi_shop.base.BaseTest;
import kaspi_shop.constants.Locator;
import kaspi_shop.pages.CategoryPage;
import kaspi_shop.pages.HomePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
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
        By activeFilterRow = By.cssSelector("[class=\"filters__filter-row _active \"]");

        int expectedCount = 0;

        for (By filter : filterLink) {
            categoryPage.click(filter);
            expectedCount++;   // после каждого клика ожидаем на 1 активный фильтр больше

            int attempts = 0;
            while (true) {
                try {
                    List<WebElement> activeFilters = categoryPage.getVisibleAll(activeFilterRow);
                    Assert.assertEquals(activeFilters.size(), expectedCount);

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

        List<WebElement> size_item_card = categoryPage.getVisibleAll(Locator.ITEM_CARD);
        Assert.assertFalse(size_item_card.isEmpty());
    }

    @Test
    public void checkSelectList () throws InterruptedException {
        categoryPage = homePage.categoryPageOpen();
        By select_list = By.cssSelector(".select__list-item");
        By actual_list = By.xpath("//div[@class=\"select__title\"]//span");

        List<WebElement> all_select_list = categoryPage.getPresentAll(select_list);
        for(int i =0; i < all_select_list.size(); i++){
            categoryPage.click(actual_list);
            categoryPage.getVisibleAll(actual_list);

            Thread.sleep(50);

            String textName = all_select_list.get(i).getText();
            all_select_list.get(i).click();
            int count = 0;

            while (true){
                try {
                    Assert.assertEquals(textName, categoryPage.getText(actual_list));
                    System.out.println(textName);
                    List<WebElement> size_item_card = categoryPage.getVisibleAll(Locator.ITEM_CARD);
                    Assert.assertFalse(size_item_card.isEmpty());
                    break;
                }catch (AssertionError e){
                    if (count == 4){
                        throw e;
                    }
                    count++;
                }
            }

        }
    }
}
