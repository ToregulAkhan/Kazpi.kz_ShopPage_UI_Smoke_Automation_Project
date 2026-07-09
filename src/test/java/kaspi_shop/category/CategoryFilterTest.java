package kaspi_shop.category;

import kaspi_shop.base.BaseTest;
import kaspi_shop.pages.CategoryPage;
import kaspi_shop.pages.HomePage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CategoryFilterTest extends BaseTest {

    @Test
    public void checkCategoryOpened(){
        CategoryPage categoryPage = homePage.categoryPageOpen();

        By allCategory = By.xpath("//a[@href=\"/shop/c/categories/\" and @class=\"tree__link\"]");
        Assert.assertEquals(categoryPage.getElement(allCategory).getText(), "Все категории");
    }
}
