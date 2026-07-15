cat > /home/claude/README_final.md << 'ENDOFFILE'
# Kaspi.kz Shop Page Smoke Automation Project

Автотесты для проверки интернет-магазина kaspi.kz **глазами пользователя**:
навигация, поиск, фильтры, сортировка, карточка товара.
Стек: **Java + Selenium WebDriver + TestNG + Maven**, паттерн **Page Object Model**.

---

## Структура проекта

```
KaspiShopAutomation/
├── src/
│   ├── main/java/kaspi_shop/
│   │   ├── base/
│   │   │   ├── BasePage.java          — общие методы для всех Page-классов
│   │   │   └── BaseTest.java          — общий setUp/tearDown браузера для всех тестов
│   │   ├── driver/
│   │   │   └── DriverManager.java     — создание/закрытие ChromeDriver
│   │   ├── utils/
│   │   │   └── WaitUtils.java         — обёртки над явными ожиданиями
│   │   ├── constants/
│   │   │   ├── Urls.java              — централизованные URL разделов сайта
│   │   │   └── Locator.java           — централизованные локаторы (By)
│   │   └── pages/
│   │       ├── HomePage.java          — главная страница
│   │       ├── SearchPage.java        — страница результатов поиска
│   │       ├── CategoryPage.java      — каталог категории (фильтры, сортировка)
│   │       ├── ProductPage.java       — карточка товара
│   │       └── CartPage.java          — корзина
│   └── main/resources/
│       └── config.properties          — base.url и настройки окружения
│
└── src/test/java/kaspi_shop/
    ├── listeners/
    │   └── TestListener.java          — логирование старт/успех/провал каждого теста
    ├── home/
    │   └── HomePageTest.java          — главная страница
    ├── search/
    │   └── SearchTest.java            — поиск товаров
    ├── category/
    │   ├── CategoryFilterTest.java    — открытие категории, фильтры, список сортировки
    │   ├── CategoryNavigationTest.java — листание слайдов карусели категорий
    │   └── CategorySortTest.java      — все виды сортировки товаров
    ├── filters/
    │   ├── BrandFilterTest.java       — фильтр по бренду
    │   ├── PriceFilterTest.java       — фильтр по цене
    │   └── SellerFilterTest.java      — фильтр по продавцу
    └── product/
        └── ProductInfTest.java        — карточка товара (ТВ/Аудио, Одежда, Мебель, Аптека)
```

---

## Классы фреймворка

### `driver.DriverManager`
Создаёт и закрывает Chrome через `static WebDriver`.
Тесты запускаются последовательно, `ThreadLocal` не нужен.

| Метод | Что делает |
|---|---|
| `initDriver()` | создаёт `ChromeDriver`, максимизирует окно. Вызывать первым. |
| `getDriver()` | возвращает уже созданный драйвер |
| `quitDriver()` | закрывает браузер и обнуляет ссылку |

> Порядок вызова: `initDriver()` → `getDriver()`. Без первого второй вернёт `null`.

---

### `base.BasePage`
Родитель для всех Page-классов. Хранит `driver` и `waitUtils`.

| Метод | Что делает |
|---|---|
| `click(By)` | ждёт кликабельности и кликает |
| `getText(By)` | ждёт видимости и возвращает текст |
| `getElement(By)` | ждёт видимости, возвращает `WebElement` |
| `getVisibleAll(By)` | ждёт видимости **всех** элементов по локатору |
| `getPresentAll(By)` | ждёт **присутствия** в DOM (без требования видимости) |
| `isDisplayed(By)` | безопасная проверка видимости, `false` вместо исключения |
| `isMoreDisplayed(By)` | проверяет, что второй элемент (`[1]`) виден |
| `waitUpdatePage(WebElement)` | ждёт `stalenessOf` переданного элемента — сигнал что AJAX-обновление страницы завершилось |
| `refreshed(By)` | ждёт обновления конкретного элемента через `ExpectedConditions.refreshed` |
| `clearAndType(By, String)` | очищает поле и вводит текст |
| `clearAndTypeAndEntry(By, String)` | то же + `Keys.ENTER` |
| `invisible(By)` | ждёт исчезновения элемента (лоадер, спиннер) |
| `getValue(By)` | возвращает атрибут `value` элемента |
| `getCurrentUrl()` / `getTitle()` | текущий URL / title страницы |

> ⚠️ Внутри метода `X` всегда вызывай `waitUtils.*` или `WebElement.*`, а не `X(...)` саму себя — иначе `StackOverflowError`.

---

### `base.BaseTest`
Родитель для всех тестовых классов.

```java
@BeforeMethod
public void setUp() {
    DriverManager.initDriver();
    driver = DriverManager.getDriver();
    homePage = new HomePage(driver);
}

@AfterMethod
public void tearDown() {
    DriverManager.quitDriver();
}
```

Каждый `@Test` стартует с чистого браузера и свежей главной страницы.

---

### `utils.WaitUtils`
Обёртка над `WebDriverWait` / `ExpectedConditions`.

| Метод | ExpectedConditions внутри | Когда использовать |
|---|---|---|
| `waitVisible(By)` | `visibilityOfElementLocated` | обычный случай |
| `waitClickable(By)` | `elementToBeClickable` | перед кликом/вводом, когда видимости мало |
| `waitVisibleAll(By)` | `visibilityOfAllElementsLocatedBy` | все элементы должны быть видимы одновременно |
| `waitPresentAll(By)` | `presenceOfAllElementsLocatedBy` | элементы есть в DOM, но скрыты (карусель) |
| `waitStaleness(WebElement)` | `stalenessOf` | ждём удаления старого узла DOM после AJAX-обновления |
| `waitRefreshed(By)` | `refreshed` | после частичной перерисовки страницы |
| `waitInvisible(By)` | `invisibilityOfElementLocated` | ждём исчезновения лоадера/спиннера |

---

### `constants.Locator`
Все локаторы (`By`) хранятся здесь — правь в одном месте при изменении вёрстки.

Примеры ключевых локаторов:
- `ITEM_CARD` — карточка товара в списке (используется как сигнал обновления страницы)
- `ACTIVE_FILTER_ROW` — активный чип применённого фильтра
- `FILTER_BRAND`, `FILTER_PRICE`, `FILTER_SELLERS` — элементы в секциях фильтров
- `BUTTON_SHOW_ELSE`, `BUTTON_HIDE_ELSE` — кнопки разворачивания/сворачивания списка фильтров
- `ACTUAL_SELECT_LIST` — кнопка открытия списка сортировки
- `POPULAR_LIST`, `NEW_LIST`, `CHEEP_PRICE_LIST`, `EXPENSIVE_PRICE_LIST`, `TOP_RATING_LIST` — пункты сортировки

---

## Page-классы

### `HomePage`
- `open()` — переход на `Urls.BASE_URL`
- `searchPage()` — переход к `SearchPage`
- `categoryPageOpen()` — переход к `CategoryPage`
- Категории рендерятся **каруселью** — вторая группа появляется в DOM только после клика по стрелке `[data-test-id='categories-next-slide']`. Для сбора всех категорий нужно кликать по стрелке в цикле и собирать видимые элементы `.category-item__title` в `Set`.

### `SearchPage`
- `inputToSearch(String)` — вводит запрос в поле поиска и жмёт Enter.

### `CategoryPage`
Основной Page-класс с логикой работы с фильтрами и сортировкой.

**Сортировка:**
открыть список (`ACTUAL_SELECT_LIST`) → кликнуть пункт → дождаться обновления товаров через `waitUpdatePage(oldItemCard)`.

**Фильтры — ключевая механика:**
```
до клика   → запомнить oldItemCard = getElement(ITEM_CARD)
клик фильтр → waitUpdatePage(oldItemCard)   // ITEM_CARD удаляется из DOM при обновлении
checkActiveRow(...)
checkItems()
до снятия  → запомнить oldItemCard2 = getElement(ITEM_CARD)
снять фильтр → waitUpdatePage(oldItemCard2) // снова ждём обновления товаров
```

> Важно: `ACTIVE_FILTER_ROW` при снятии фильтра **не удаляется** из DOM — сайт просто скрывает его через CSS. Поэтому `waitUpdatePage(oldActiveRow)` не сработает — всегда ждать обновления `ITEM_CARD`.

**Метод `choseAndCheckOneItemInFilter(int index, By filterLocator)`** — универсальная проверка одного значения фильтра: кликнуть → дождаться обновления → проверить активный чип → проверить наличие товаров → снять фильтр → дождаться обновления.

**Метод `checkActiveRow(String)`** — проверяет, что переданный текст содержится **хотя бы в одном** из активных чипов (`anyMatch`), а не только в первом.

**Метод `filterSize(By filterLocator)`** — возвращает актуальное количество элементов в конкретной секции фильтра с retry на `StaleElementReferenceException`.

**Метод `entryToListItem(By locator)`** — открывает каталог, выбирает вид сортировки, кликает первую карточку товара (с retry на `StaleElementReferenceException`).

### `ProductPage`
Карточка товара. Тесты в `ProductInfTest` открывают товары из разных категорий: ТВ/Аудио, Одежда, Мебель, Аптека.

### `CartPage`
Минимальный класс. Расширяется по мере необходимости.

---

## Тестовые классы

### `home.HomePageTest`
| Тест | Что проверяет |
|---|---|
| `checkNavigationItem` | пункты верхнего меню категорий содержат ожидаемые названия |
| `checkCategoryItem` | все категории в карусели на главной (с пролистыванием слайдов) |
| `chooseCity` | смена города (Семей → Астана → Алматы) |
| `checkLogoDisplayed` | логотип Kaspi виден на главной |

### `search.SearchTest`
| Тест | Что проверяет |
|---|---|
| `checkSearchWorks` | по запросу "книга" находятся товары (несколько карточек) |

### `category.CategoryFilterTest`
| Тест | Что проверяет |
|---|---|
| `checkCategoryOpened` | страница категории открывается и содержит товары |
| `checkFilters` | фильтры по цене/бренду/продавцу применяются, активный чип появляется |
| `checkSelectList` | список сортировки открывается |

### `category.CategoryNavigationTest`
| Тест | Что проверяет |
|---|---|
| `checkNextSlide` | карусель листается вперёд, показывается следующий слайд |
| `checkPrevious` | карусель листается назад |

### `category.CategorySortTest`
| Тест | Что проверяет |
|---|---|
| `checkSortingPopular` | сортировка "Популярные" — у первого товара есть рейтинг |
| `checkSortingNew` | сортировка "Новинки" — у первого товара нет рейтинга (новые) |
| `checkSortingCheep` | сортировка "Сначала дешёвые" — открывается без ошибок |
| `checkSortingExpansive` | сортировка "Сначала дорогие" — открывается без ошибок |
| `checkSortingTopRating` | сортировка "Высокий рейтинг" — открывается без ошибок |

### `filters.BrandFilterTest`
Проверяет фильтр по бренду: первый, средний, последний бренд из списка.
Список брендов **сужается** после применения фильтра (co-filtering Kaspi) — поэтому каждая итерация начинается с заново открытой страницы категории. Используется retry-цикл на `StaleElementReferenceException` при получении элемента бренда.

### `filters.PriceFilterTest`
Проверяет каждый ценовой диапазон из фильтра: кликает → проверяет активный чип → проверяет что цена первого товара попадает в выбранный диапазон → снимает фильтр. Логика парсинга цены: `Integer.parseInt(priceText.replaceAll("\\D", ""))`.

### `filters.SellerFilterTest`
Проверяет фильтр по продавцу: первый, средний, последний продавец.
Использует `filterSize(FILTER_SELLERS)` — не `allFilterSize()`, чтобы не перепутать с размером списка брендов.

### `product.ProductInfTest`
| Тест | Что проверяет |
|---|---|
| `checkTvAudioProduct` | карточка товара из категории ТВ, Аудио, Видео |
| `checkFashionProduct` | карточка товара из категории Одежда |
| `checkFurnitureProduct` | карточка товара из категории Мебель |
| `checkPharmacyProduct` | карточка товара из категории Аптека |

### `listeners.TestListener`
Логирует в консоль старт (`▶`), успех (`✅`), провал (`❌`) и пропуск (`⏭`) каждого теста. Подключён через `<listeners>` в `testng.xml`.

---

## Ключевые архитектурные решения

**`waitUpdatePage(WebElement oldElement)` как универсальный сигнал обновления страницы**
После любого AJAX-действия (клик по фильтру, смена сортировки) правильный способ дождаться завершения обновления — запомнить ссылку на `ITEM_CARD` до действия, а после — вызвать `waitUpdatePage(oldItemCard)`. Список товаров пересоздаётся в DOM при любом изменении → старый узел "протухает" → `stalenessOf` срабатывает. Активные чипы фильтров (`ACTIVE_FILTER_ROW`) **не пересоздаются** — только скрываются/показываются через CSS, поэтому для ожидания снятия фильтра тоже используется `ITEM_CARD`, а не сам чип.

**Co-filtering в фильтре брендов**
После применения фильтра по бренду Kaspi автоматически сужает список остальных фильтров до тех, что пересекаются с выбранным брендом. Тест брендов обходит это через перезагрузку страницы перед каждой итерацией — так список всегда полный.

**Репрезентативная выборка вместо полного перебора**
В фильтрах тестируется не каждое значение, а выборка: первый, средний, последний элемент. Это позволяет быстро проверить механизм фильтрации, не превращая smoke-тест в 20-минутный прогон.

---

## Частые проблемы и их решения

| Симптом | Причина | Решение |
|---|---|---|
| `StackOverflowError` в `getText` / аналогичном методе | метод вызывал сам себя вместо `waitUtils.*` | внутри метода `X` всегда обращаться к `waitUtils.*` или `WebElement.*`, не к `X(...)` |
| `NullPointerException: this.homePage is null` | поле `homePage` переобъявлено в тестовом классе (shadowing) | не объявлять заново поля, уже существующие в родителе `BaseTest` |
| `RuntimeException: config.properties не найден` | файл лежал в `src/resources` вместо `src/main/resources` | Maven ищет ресурсы строго по стандартным путям |
| `TimeoutException` на `waitVisibleAll` | элемент скрыт стилями, но присутствует в DOM (карусель, свёрнутый список) | переключиться на `waitPresentAll`, читать `getAttribute("textContent")` вместо `getText()` |
| `TimeoutException` на `waitStaleness(ACTIVE_FILTER_ROW)` | сайт скрывает чип через CSS, не удаляет из DOM | ждать staleness `ITEM_CARD`, а не самого чипа фильтра |
| `StaleElementReferenceException` при клике/чтении | DOM перерисовался между `findElement` и действием над элементом | запрашивать элемент заново непосредственно перед действием; retry-цикл `for (attempt=1; attempt<=3; attempt++)` |
| `IndexOutOfBoundsException: Index N out of bounds for length N` | `all_size` считался по локатору другого фильтра (например, брендов вместо продавцов) | передавать конкретный локатор в `filterSize(By filterLocator)`, не использовать одну переменную для разных фильтров |
| `AssertionError: Expected "до 10 000 т" / Actual "10 000 - 49 999 т"` | `getText(ACTIVE_FILTER_ROW)` брал **первый** активный чип, а не тот, что только что применили | в `checkActiveRow` использовать `anyMatch` по всем активным чипам, а не `getText` первого |
| CSS-локатор не находит элемент с несколькими классами | `[class="a b"]` требует точного совпадения всей строки | использовать `.a.b` (классы через точку) или `contains(@class,'a')` в xpath |
| Тест нестабилен на медленной загрузке | `Thread.sleep(N)` — ждёт фиксированное время | заменять на `waitClickable`/`waitVisible`/`waitUpdatePage` — ждать конкретное событие |

---

## Запуск

```bash
mvn clean test
```

Перед первым прогоном проверь актуальность локаторов в `Locator.java` через DevTools — вёрстка Kaspi может обновиться.
ENDOFFILE
echo ok