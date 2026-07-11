package kaspi_shop.constants;

import org.openqa.selenium.By;

public class Locator {

    public static final By ALL_CATEGORIES_BUTTON = By.xpath("//a[@class=\"navigation__link navigation__link_all-categories\"]");
    public static final By SEARCH_INPUT = By.cssSelector("[id=\"search-bar-input\"]");
    public static final By SEARCH_BUTTON = By.cssSelector("[class=\"search-bar__submit-button\"]");
    public static final By OVERLAY = By.cssSelector(".search-bar-overlay");
    public static final By ITEM_CARD = By.cssSelector("[class=\"item-card ddl_product ddl_product_link undefined \"]");
    public static final By SELECT_LIST = By.cssSelector(".select__list-item");
    public static final By ACTUAL_SELECT_LIST = By.xpath("//div[@class=\"select__title\"]//span");
    public static final By ACTIVE_FILTER_ROW = By.cssSelector("[class=\"filters__filter-row _active \"]");
    public static final By PRICE_10000 = By.xpath("//span[text()='до 10 000 т']");
    public static final By PRICE_100000_149999 = By.xpath("//span[text()='100 000 - 149 999 т']");
    public static final By PRICE_500000 = By.xpath("//span[text()='более 500 000 т']");
    public static final By SAMSUNG = By.xpath("//span[text()='Samsung']");
    public static final By NIKE = By.xpath("//span[text()='Nike']");
    public static final By LEGO = By.xpath("//span[text()='LEGO']");
    public static final By SULPAK = By.xpath("//span[text()='Sulpаk']");
    public static final By SELLERS = By.xpath("//span[text()='Продавцы']/following-sibling::div//div[contains(@class,'filters__filter-row')]");

}
