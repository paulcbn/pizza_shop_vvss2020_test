package pizzashop.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.service.PizzaService;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryMockTest {

    private final String filename = "test_payments.txt";
    private PaymentRepository repository;

    @Mock
    private Payment payment;

    private void refreshInputFile() throws IOException {
        MockitoAnnotations.initMocks(this);


        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        File inputFile = new File(classLoader.getResource(filename).getFile());

        if (inputFile.exists() && !inputFile.delete())
            throw new IOException("File already existed but could not be deleted.");

        if (!inputFile.createNewFile())
            throw new IOException("New file could not be created.");
    }


    @BeforeEach
    void setUp() throws IOException {
        refreshInputFile();
        repository = new PaymentRepository(filename);
    }

    @AfterEach
    void tearDown() throws IOException {
        refreshInputFile();
    }

    @Test
    void testAddHappyFlow() {
        Mockito.when(payment.toString()).thenReturn("0,CARD,12");

        repository.add(payment);

        assertTrue(repository.getAll().contains(payment));
    }


    @Test
    void testAddNullPayment() {
        Mockito.when(payment.toString()).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> repository.add(payment));
    }

}