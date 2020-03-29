package pizzashop.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PizzaServiceTest {

    private final String filename = "test_payments.txt";
    private PaymentRepository paymentRepository;
    private PizzaService pizzaService;

    private void refreshInputFile() throws IOException {
        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        File inputFile = new File(classLoader.getResource(filename).getFile());

        if (inputFile.exists() && !inputFile.delete())
            throw new IOException("File already existed but could not be deleted.");

        if (!inputFile.createNewFile())
            throw new IOException("New file could not be created.");
    }


    private boolean checkRepoContainsPayment(int tableNumber, PaymentType type, double amount) {
        if (paymentRepository == null)
            return false;
        return paymentRepository.getAll().stream()
                .anyMatch(p -> p.getAmount() == amount && p.getType() == type && p.getTableNumber() == tableNumber);
    }

    @BeforeEach
    void setUp() throws IOException {
        refreshInputFile();
        paymentRepository = new PaymentRepository(filename);
        MenuRepository menuRepository = new MenuRepository();
        pizzaService = new PizzaService(menuRepository, paymentRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        refreshInputFile();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class ECP_AddPayment_tests {

        @Test
        @DisplayName("TC01_ECP: Happy flow for adding payment.")
        void happyFlow() {
            pizzaService.addPayment(1, PaymentType.CASH, 5);
            assertTrue(checkRepoContainsPayment(1, PaymentType.CASH, 5));
        }

        @DisplayName("TC02_ECP: Table codes smaller or equal to zero are invalid.")
        @ParameterizedTest(name = "For example, code {0} is not supported.")
        @ValueSource(ints = {-1, -2, -3})
        void tooSmallTableNumber(int tableCode) {
            assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(tableCode, PaymentType.CASH, 5));
            assertFalse(checkRepoContainsPayment(tableCode, PaymentType.CASH, 5));
        }

        @DisplayName("TC03_ECP: Table codes greater or equal to 9 are invalid.")
        @ParameterizedTest(name = "For example, code {0} is not supported.")
        @ValueSource(ints = {9, 12, 20})
        void tooLargeTableNumber(int tableCode) {
            assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(tableCode, PaymentType.CASH, 5));
            assertFalse(checkRepoContainsPayment(tableCode, PaymentType.CASH, 5));
        }

        @DisplayName("TC04_ECP: Payment amounts smaller or equal to zero are invalid")
        @ParameterizedTest(name = "For example, amount {0} is not supported.")
        @ValueSource(doubles = {0.0, -12.0, -133.0})
        void tooSmallAmount(double amount) {
            assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(1, PaymentType.CASH, amount));
            assertFalse(checkRepoContainsPayment(1, PaymentType.CASH, amount));
        }

        @Test
        @DisplayName("TC05_ECP: Too small payment amount and too small table number")
        void tooSmallAmountAndTableNumber() {
            assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(-1, PaymentType.CASH, -1));
            assertFalse(checkRepoContainsPayment(-1, PaymentType.CASH, -1));
        }

        @Test
        @DisplayName("TC05_ECP: Too small payment amount and too large table number")
        void tooSmallAmountAndTooLargeTableNumber() {
            assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(9, PaymentType.CASH, -1));
            assertFalse(checkRepoContainsPayment(9, PaymentType.CASH, -1));
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @TestMethodOrder(MethodOrderer.Alphanumeric.class)
    class BVA_AddPayment_tests {

        @Nested
        @DisplayName("CONDITION 1: table code has to be between 1 and 8 inclusive.")
        class TableConditionTest {
            @Test
            @DisplayName("TC01_BVA: On the lower boundary (1).")
            void onTheLowerBoundary() {
                pizzaService.addPayment(1, PaymentType.CASH, 5);
                assertTrue(checkRepoContainsPayment(1, PaymentType.CASH, 5));
            }

            @Test
            @DisplayName("TC02_BVA: Bellow the lower boundary (0).")
            void bellowTheLowerBoundary() {
                assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(0, PaymentType.CASH, 5));
                assertFalse(checkRepoContainsPayment(0, PaymentType.CASH, 5));
            }

            @Test
            @DisplayName("TC03_BVA: Above the lower boundary (2).")
            void aboveTheLowerBoundary() {
                pizzaService.addPayment(2, PaymentType.CASH, 5);
                assertTrue(checkRepoContainsPayment(2, PaymentType.CASH, 5));
            }

            @Test
            @DisplayName("TC04_BVA: On the upper boundary (8).")
            void onTheUpperBoundary() {
                pizzaService.addPayment(8, PaymentType.CASH, 5);
                assertTrue(checkRepoContainsPayment(8, PaymentType.CASH, 5));
            }

            @Test
            @DisplayName("TC05_BVA: Above the upper boundary (9).")
            void bellowTheUpperBoundary() {
                assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(9, PaymentType.CASH, 5));
                assertFalse(checkRepoContainsPayment(9, PaymentType.CASH, 5));
            }

            @Test
            @DisplayName("TC06_BVA: Bellow the upper boundary (7).")
            void aboveTheUpperBoundary() {
                pizzaService.addPayment(7, PaymentType.CASH, 5);
                assertTrue(checkRepoContainsPayment(7, PaymentType.CASH, 5));
            }
        }


        @Nested
        @DisplayName("CONDITION 2: payment amount strictly greater than 0.")
        @TestMethodOrder(MethodOrderer.Alphanumeric.class)
        class PaymentConditionTest {
            @Test
            @DisplayName("TC07_BVA: On the lower boundary (0).")
            void onTheLowerBoundary() {
                assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(1, PaymentType.CASH, 0));
                assertFalse(checkRepoContainsPayment(1, PaymentType.CASH, 0));
            }

            @Test
            @DisplayName("TC08_BVA: Above the lower boundary (1).")
            void bellowTheLowerBoundary() {
                pizzaService.addPayment(1, PaymentType.CASH, 1);
                assertTrue(checkRepoContainsPayment(1, PaymentType.CASH, 1));
            }

            @Test
            @DisplayName("TC09_BVA: Bellow the lower boundary (-1).")
            void aboveTheLowerBoundary() {
                assertThrows(IllegalArgumentException.class, () -> pizzaService.addPayment(1, PaymentType.CASH, -1));
                assertFalse(checkRepoContainsPayment(1, PaymentType.CASH, -5));
            }
        }
    }

}