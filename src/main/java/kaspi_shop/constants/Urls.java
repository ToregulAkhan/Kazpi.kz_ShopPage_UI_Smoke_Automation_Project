package kaspi_shop.constants;

import kaspi_shop.utils.ConfigReader;

public class Urls {

    public static final String BASE_URL = ConfigReader.get("base.url");

    public static final String CATALOG = BASE_URL + "/almaty/c/categories/";

}
