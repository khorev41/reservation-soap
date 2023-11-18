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
        int appointmentID = 11;
        try {
            ReservationResponse reservationResponse = client.makeReservation("123456/7890", appointmentID);

            assertEquals(11, reservationResponse.getAppointmentID());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            Appointment appointment = appointmentService.getAppointmentById(11).get();
            appointment.setPatientId(null);
            appointmentService.saveAppointment(appointment);
        }
    }

    @Test
    public void testThrowErrorIfAppointmentNotPresentInDB() {
        int appointmentID = Integer.MAX_VALUE;
        try {
            Optional<Appointment> appointmentById = appointmentService.getAppointmentById(appointmentID);

            if (appointmentById.isEmpty()) {
                assertThrows(ReservationFaultMessage.class, () -> client.makeReservation("123", appointmentID));
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testReservationHaveCorrectPatientId() {
        Optional<Appointment> optionalAppointment = null;
        try {
            String patientId = "123456/7890";
            ReservationResponse reservationResponse = client.makeReservation(patientId, 11);
            optionalAppointment = appointmentService.getAppointmentById(reservationResponse.getAppointmentID());

            if (optionalAppointment.isPresent()) {
                assertEquals(reservationResponse.getPatientID(), patientId);
                assertEquals(optionalAppointment.get().getPatientId(), patientId);

                Appointment appointment = optionalAppointment.get();
                appointment.setPatientId(null);
                appointmentService.saveAppointment(appointment);
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testReservationReallySavedToDB() {
        int appointmentId = 10;
        String patientId = "123456/7890";
        try {
            ReservationResponse reservationResponse = client.makeReservation(patientId, appointmentId);

            Optional<Appointment> optionalAppointment = appointmentService.getAppointmentById(reservationResponse.getAppointmentID());
            optionalAppointment.ifPresent(appointment -> assertEquals(appointment.getPatientId(), patientId));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId).get();
            appointment.setPatientId(null);
            appointmentService.saveAppointment(appointment);
        }
    }

    @Test
    public void testMakeReservationWithInvalidAppointmentIDThrowsReservationFaultMessage() {
        ReservationServiceImpl reservationService = new ReservationServiceImpl();
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setPatientID("123456/7890");
        reservationRequest.setAppointmentID(0);

        assertThrows(ReservationFaultMessage.class, () -> reservationService.makeReservation(reservationRequest));
    }

    @Test
    public void testMakeReservationWithNonexistentAppointmentIDThrowsReservationFaultMessage() {
        String patientID = "123456/7890";
        int appointmentID = 999;

        assertThrows(ReservationFaultMessage.class, () -> client.makeReservation(patientID, appointmentID));
    }

    @Test
    public void testMakeReservationWithAlreadyReservedAppointmentThrowsReservationFaultMessage() {
        try {
            int appointmentID = 11;
            try {
                client.makeReservation("123", appointmentID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertThrows(ReservationFaultMessage.class, () -> client.makeReservation("abc", appointmentID));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            Appointment appointment = appointmentService.getAppointmentById(11).get();
            appointment.setPatientId(null);
            appointmentService.saveAppointment(appointment);
        }
    }


}
