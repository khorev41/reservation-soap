package sk.upjs.ics.kopr;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.upjs.ics.kopr.entities.Appointment;
import sk.upjs.ics.kopr.services.AppointmentService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Launcher.class)
public class ReservationServiceClientTest {

    private static ReservationServiceClient client;
    @Autowired
    AppointmentService appointmentService;

    @BeforeAll
    public static void setUp() {
        Launcher.main(new String[]{});
        client = new ReservationServiceClient();
    }

    @Test
    public void testReservationHaveCorrectAppointmentId() {
        try {
            ReservationResponse reservationResponse = client.makeReservation("123456/7890", 11);

            assertTrue(reservationResponse.getAppointmentID() == 11);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testReservationWillHaveIdZeroIfNotPresentInDB() {
        try {
            ReservationResponse reservationResponse = client.makeReservation("123456/7890", 11);
            Optional<Appointment> appointmentById = appointmentService.getAppointmentById(11);

            if(!appointmentById.isPresent()){
                assertTrue(reservationResponse.getAppointmentID() == 0);
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testReservationHaveCorrectPatientId() {
        try {
            String patientId = "123456/7890";
            ReservationResponse reservationResponse = client.makeReservation(patientId, 11);
            Optional<Appointment> optionalAppointment = appointmentService.getAppointmentById(reservationResponse.getAppointmentID());

            if(optionalAppointment.isPresent()){
                assertTrue(reservationResponse.getPatientID().equals(patientId));
                assertTrue(optionalAppointment.get().getPatientId().equals(patientId));
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testReservationReallySavedToDB() {
        try {
            String patientId = "123456/7890";
            ReservationResponse reservationResponse = client.makeReservation(patientId, 10);

            Optional<Appointment> optionalAppointment = appointmentService.getAppointmentById(reservationResponse.getAppointmentID());
            if(optionalAppointment.isPresent()){
                assertTrue(optionalAppointment.get().getPatientId().equals(patientId));
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }



}
