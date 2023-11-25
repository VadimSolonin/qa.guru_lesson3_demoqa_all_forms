package tests.api;

import io.qameta.allure.Owner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import pages.ProfilePage;
import tests.api.apiResponses.AuthorizationApi;
import tests.api.apiResponses.BooksApi;
import tests.api.models.*;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;

@Owner("VS")
@Tag("api")
public class ProfileBooksListTests extends TestBase {
    AuthorizationApi authorizationApi = new AuthorizationApi();
    BooksApi booksApi = new BooksApi();
    LoginRequestModel loginRequestModel = new LoginRequestModel(TestData.LOGIN, TestData.PASSWORD);
    LoginResponseModel loginResponse = authorizationApi.login(loginRequestModel);
    ProfilePage profilePage = new ProfilePage();

    @Test
    void addBookToProfileTest() {
        step("Удаление всех книг в коллекции", () -> {
            booksApi.deleteAllBooks(loginResponse);
        });
        step("Добавление книги в каталог", () -> {
            IsbnModel isbnModel = new IsbnModel("9781449325862");
            List<IsbnModel> isbnList = new ArrayList<>();
            isbnList.add(isbnModel);

            AddBooksListRequestModel booksList = new AddBooksListRequestModel();
            booksList.setUserId(loginResponse.getUserId());
            booksList.setCollectionOfIsbns(isbnList);

            booksApi.addBook(loginResponse, booksList);
        });
        step("Открытие страницы сайта и добавление cookie", () -> {
            open("/favicon.ico");
            getWebDriver().manage().addCookie(new Cookie("userID", loginResponse.getUserId()));
            getWebDriver().manage().addCookie(new Cookie("token", loginResponse.getToken()));
            getWebDriver().manage().addCookie(new Cookie("expires", loginResponse.getExpires()));
        });
        step("Открытие страницы через UI и проверка наличия книги", () -> {
            open("/profile");
            profilePage.verifyGitPocketBook();
        });

    }

    @Test
    void deleteBookFromProfileTest() {
        step("Удаление всех книг в коллекции", () -> {
            booksApi.deleteAllBooks(loginResponse);
        });
        step("Добавление в коллекцию книги", () -> {
            List<IsbnModel> isbnList = new ArrayList<>();
            IsbnModel isbnModel = new IsbnModel("9781449325862");
            isbnList.add(isbnModel);

            AddBooksListRequestModel booksList = new AddBooksListRequestModel();
            booksList.setUserId(loginResponse.getUserId());
            booksList.setCollectionOfIsbns(isbnList);

            booksApi.addBook(loginResponse, booksList);
        });
        step("Удаление конкретной книги", () -> {
            DeleteBookResponseModel deleteBookModel = new DeleteBookResponseModel("9781449325862", "1bac20f1-55eb-4cdc-b546-957c96333466");
            booksApi.deleteBook(loginResponse, deleteBookModel);
        });
        step("Открытие страницы сайта и добавление cookie", () -> {
            open("/favicon.ico");
            getWebDriver().manage().addCookie(new Cookie("userID", loginResponse.getUserId()));
            getWebDriver().manage().addCookie(new Cookie("token", loginResponse.getToken()));
            getWebDriver().manage().addCookie(new Cookie("expires", loginResponse.getExpires()));
        });
        step("Открытие страницы через UI и проверка отсутствия книги", () -> {
            open("/profile");
            profilePage.verifyNoDataTableText();
        });
    }
}
