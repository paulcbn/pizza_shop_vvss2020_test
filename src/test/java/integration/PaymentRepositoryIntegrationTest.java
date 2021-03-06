package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.io.File;
import java.io.IOException;

class PaymentRepositoryIntegrationTest {

    private final String filename = "test_payments.txt";
    private PaymentRepository paymentRepository;
    private PizzaService pizzaService;

    @Mock
    private Payment payment1, payment2;

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
        MockitoAnnotations.initMocks(this);
        refreshInputFile();
        paymentRepository = new PaymentRepository(filename);
        MenuRepository menuRepository = new MenuRepository();
        pizzaService = new PizzaService(menuRepository, paymentRepository);
        Mockito.when(payment1.getTableNumber()).thenReturn(1);
        Mockito.when(payment1.getAmount()).thenReturn(100.0);
        Mockito.when(payment1.getType()).thenReturn(PaymentType.CASH);

        Mockito.when(payment2.getTableNumber()).thenReturn(2);
        Mockito.when(payment2.getAmount()).thenReturn(44.0);
        Mockito.when(payment2.getType()).thenReturn(PaymentType.CASH);
    }

    @AfterEach
    void tearDown() throws IOException {
        refreshInputFile();
    }

    @Test
    void testGetTotalAmount() {
        paymentRepository.add(payment1);
        paymentRepository.add(payment2);
        assert (pizzaService.getTotalAmount(PaymentType.CASH) == 144.0);

    }

    @Test
    void testAdd() {
        assert (pizzaService.getPayments().size() == 0);
        assert (paymentRepository.getAll().size() == 0);
        paymentRepository.add(payment1);
        paymentRepository.add(payment2);
        assert (pizzaService.getPayments().size() == 2);
        assert (paymentRepository.getAll().size() == 2);
        pizzaService.addPayment(3, PaymentType.CARD, 50);
        assert (pizzaService.getPayments().size() == 3);
        assert (paymentRepository.getAll().size() == 3);
    }
}