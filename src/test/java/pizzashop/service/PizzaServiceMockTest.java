package pizzashop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.PaymentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PizzaServiceMockTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PizzaService service;

    @Captor
    private ArgumentCaptor<Payment> cPayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_add_happy_flow() {
        Mockito.doNothing().when(paymentRepository).add(Mockito.any());

        service.addPayment(1, PaymentType.CASH, 20.35);

        Mockito.verify(paymentRepository, Mockito.times(1)).add(cPayment.capture());

        assertEquals(1, cPayment.getValue().getTableNumber());
        assertEquals(PaymentType.CASH, cPayment.getValue().getType());
        assertEquals(20.35, cPayment.getValue().getAmount());
    }

    @Test
    void test_add_negative_amount() {
        Mockito.doNothing().when(paymentRepository).add(Mockito.any());

        assertThrows(IllegalArgumentException.class, () -> service.addPayment(1, PaymentType.CASH, -20.35));

        Mockito.verify(paymentRepository, Mockito.never()).add(cPayment.capture());
    }


}