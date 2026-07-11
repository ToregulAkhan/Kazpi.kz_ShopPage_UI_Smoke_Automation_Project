package kaspi_shop.category;

import kaspi_shop.base.BaseTest;
import kaspi_shop.constants.Locator;
import kaspi_shop.pages.CategoryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;

public class CategorySortTest extends BaseTest {
    protected CategoryPage categoryPage;

    public void checkSorting() throws InterruptedException {
        categoryPage = homePage.categoryPageOpen();

        List<WebElement> all_select_list = categoryPage.getPresentAll(Locator.SELECT_LIST);

        for(int i =0; i < all_select_list.size(); i++){

            categoryPage.click(Locator.ACTUAL_SELECT_LIST);
            categoryPage.getVisibleAll(Locator.ACTUAL_SELECT_LIST);
            Thread.sleep(200);
            all_select_list.get(i).click();




        }
    }
}
