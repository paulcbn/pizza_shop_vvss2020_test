package integration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryIntegrationTest {

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
    void testTrue() {
        assertTrue(true);
    }

}