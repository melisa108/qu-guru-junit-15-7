package junit.homework;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import junit.homework.data.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class WebTest {

    @BeforeAll
    static void setUp() {
        open("https://www.collinsdictionary.com");
        $(".banner-actions-container").$(byText("I Accept")).click();
        Configuration.holdBrowserOpen = true;
    }

    static Stream<Arguments> collinsDictionaryButtonsText() {
        return Stream.of(
                Arguments.of(List.of("LANGUAGE", "TRANSLATOR", "GAMES", "SCHOOLS", "BLOG", "More", "English", "French", "German", "Italian", "Spanish", "Portuguese", "More"), Locale.English),
                Arguments.of(List.of("LINGUA", "TRADUTTORE", "GIOCHI", "SCUOLE", "BLOG", "RISORSE", "Inglese", "Francese", "Tedesco", "Italiano", "Spagnolo", "Di Più"), Locale.Italiano)
        );

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

    @DisplayName("Valid search of first result")
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

    @MethodSource
    @ParameterizedTest(name = "Check headers localization: {1}")
    void collinsDictionaryButtonsText(List<String> buttonsTexts, Locale locale) {
        $$(".t-p-i_select option").find(text(locale.name())).click();
        $$(".major-links-container a").filter(visible)
                .shouldHave(CollectionCondition.texts(buttonsTexts));
    }

    @EnumSource(Locale.class)
    @ParameterizedTest(name = "Check options of localization")
    void checkLocaleTest(Locale locale) {
        $$(".t-p-i_select option").find(text(locale.name())).shouldBe(visible);
    }
}