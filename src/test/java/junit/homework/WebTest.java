package junit.homework;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import qa.guru.data.Locale;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class WebTest {

    @BeforeAll
    static void setUp() {
        open("https://www.collinsdictionary.com");
        $(".banner-actions-container").$(byText("I Accept")).click();
        Configuration.holdBrowserOpen = true;
    }

    @DisplayName("Valid search with 1 word")
    @ParameterizedTest(name = "Check the headers of search input {0}")
    @ValueSource(strings = {"engine", "industry"})
    void collinsDictionarySearchTest(String testData) {
        $("input[type='text']").setValue(testData).pressEnter();
        $$(".title_container")
                .shouldHave(CollectionCondition.sizeGreaterThanOrEqual(3))
                .first()
                .shouldHave(text(testData));
    }

    @DisplayName("Valid search of first result ")
    @ParameterizedTest(name = "Check the headers and texts of search input {0}")
    @CsvSource({
            "engine, The engine of a car or other vehicle is the part that produces the power",
            "industry, Industry is the work and processes involved in collecting raw materials"
    })
    void collinsDictionarySearchTestDifferentExpectedText(String searchQuery, String expectedText) {
        $("input[type='text']").setValue(searchQuery);
        $("button[type='submit']").click();
        $(".hom").shouldHave(text(expectedText));
    }

    @Test
    void local(){
       $(".t-p-i_select").$(byText("Italiano")).click();
    }

    @CsvSource({
            "Selenide, Selenide - это фреймворк для автоматизированного тестирования",
            "JUnit, junit.org"
    })
    @ParameterizedTest(name = "Проверка числа результатов поиска в Яндексе для запроса {0}")
    void yandexSearchCommonTestDifferentExpectedText(String searchQuery, String expectedText) {
        open("https://ya.ru");
        $("#text").setValue(searchQuery);
        $("button[type='submit']").click();
        $$("li.serp-item")
                .shouldHave(CollectionCondition.sizeGreaterThanOrEqual(10))
                .first()
                .shouldHave(text(expectedText));
    }

    static Stream<Arguments> selenideSiteButtonsTextDataProvider() {
        return Stream.of(
                Arguments.of(List.of("Quick start", "Docs", "FAQ", "Blog", "Javadoc", "Users", "Quotes"), Locale.EN),
                Arguments.of(List.of("С чего начать?", "Док", "ЧАВО", "Блог", "Javadoc", "Пользователи", "Отзывы"), Locale.RU)
        );

    }

    @MethodSource("selenideSiteButtonsTextDataProvider")
    @ParameterizedTest(name = "Проверка отображения названия кнопок для локали: {1}")
    void selenideSiteButtonsText(List<String> buttonsTexts, Locale locale) {
        open("https://selenide.org/");
        $$("#languages a").find(text(locale.name())).click();
        $$(".main-menu-pages a").filter(visible)
                .shouldHave(CollectionCondition.texts(buttonsTexts));
    }

    @EnumSource(Locale.class)
    @ParameterizedTest
    void checkLocaleTest(Locale locale) {
        open("https://selenide.org/");
        $$("#languages a").find(text(locale.name())).shouldBe(visible);
    }
}