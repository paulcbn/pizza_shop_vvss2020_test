package pizzashop.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.log4j.Logger;
import pizzashop.model.PaymentType;

import java.util.Optional;

public class PaymentAlert implements PaymentOperation {
    private PizzaService service;
    static final Logger logger = Logger.getLogger(PaymentAlert.class);

    private static final String BAR = "--------------------------";

    public PaymentAlert(PizzaService service) {
        this.service = service;
    }

    @Override
    public void cardPayment() {
        logger.info(BAR);
        logger.info("Paying by card...");
        logger.info("Please insert your card!");
        logger.info(BAR);
    }

    @Override
    public void cashPayment() {
        logger.info(BAR);
        logger.info("Paying cash...");
        logger.info("Please show the cash...!");
        logger.info(BAR);
    }

    @Override
    public void cancelPayment() {
        logger.info(BAR);
        logger.info("Payment choice needed...");
        logger.info(BAR);
    }

    public void showPaymentAlert(int tableNumber, double totalAmount) {
        Alert paymentAlert = new Alert(Alert.AlertType.CONFIRMATION);
        paymentAlert.setTitle("Payment for Table " + tableNumber);
        paymentAlert.setHeaderText("Total amount: " + totalAmount);
        paymentAlert.setContentText("Please choose payment option");
        ButtonType cardPayment = new ButtonType("Pay by Card");
        ButtonType cashPayment = new ButtonType("Pay Cash");
        ButtonType cancel = new ButtonType("Cancel");
        paymentAlert.getButtonTypes().setAll(cardPayment, cashPayment, cancel);
        Optional<ButtonType> result = paymentAlert.showAndWait();
        if (!result.isPresent())
            cancelPayment();
        else if (result.get() == cardPayment) {
            cardPayment();
            service.addPayment(tableNumber, PaymentType.CARD, totalAmount);
        } else if (result.get() == cashPayment) {
            cashPayment();
            service.addPayment(tableNumber, PaymentType.CASH, totalAmount);
        } else if (result.get() == cancel) {
            cancelPayment();
        } else {
            cancelPayment();
        }
    }
}
