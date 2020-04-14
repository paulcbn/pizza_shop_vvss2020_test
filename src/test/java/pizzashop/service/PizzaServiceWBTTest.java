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

class PizzaServiceWBTTest {

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

    @Test
    @DisplayName("F02_T02: Empty payment list")
    void noPayments() {
        //Act
        double value = pizzaService.getTotalAmount(PaymentType.CASH);

        //Assert
        assertEquals(0, value);
    }

    @Test
    @DisplayName("F02_T03: One payment existing. One payment found.")
    void onePaymentLookup() {
        //Arrange
        pizzaService.addPayment(1, PaymentType.CASH, 1);

        //Act
        double value = pizzaService.getTotalAmount(PaymentType.CASH);

        //Assert
        assertEquals(1, value);
    }

    @Test
    @DisplayName("F02_T04: One payment existing. No payment found.")
    void noPaymentLookup() {
        //Arrange
        pizzaService.addPayment(1, PaymentType.CARD, 1);

        //Act
        double value = pizzaService.getTotalAmount(PaymentType.CASH);

        //Assert
        assertEquals(0, value);
    }


    @Test
    @DisplayName("F02_T05: Two loops. All payments found.")
    void twoPaymentsLookup() {
        //Arrange
        pizzaService.addPayment(1, PaymentType.CARD, 1);
        pizzaService.addPayment(1, PaymentType.CARD, 1);

        //Act
        double value = pizzaService.getTotalAmount(PaymentType.CARD);

        //Assert
        assertEquals(2, value);
    }

    @DisplayName("F02_T06: N loops. N payments found.")
    @ParameterizedTest(name = "For example, for {0} CARD payments of value 1, expected value is {0}.  ")
    @ValueSource(ints = {3, 4, 10})
    void nPaymentLookup(int n) {
        //Arrange
        for (int i = 0; i < n; i++)
            pizzaService.addPayment(1, PaymentType.CARD, 1);

        //Act
        double value = pizzaService.getTotalAmount(PaymentType.CARD);

        //Assert
        assertEquals(n, value);
    }


}