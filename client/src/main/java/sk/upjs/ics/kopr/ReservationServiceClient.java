package sk.upjs.ics.kopr;

import java.net.URL;

public class ReservationServiceClient {

    public sk.upjs.ics.kopr.ReservationResponse makeReservation(String patientId, int appointmentId) throws Exception {
        URL url = new URL("http://localhost:8888/reservation?wsdl");

        ReservationService reservationService = new ReservationService(url);

        ReservationRequest request = new ReservationRequest();
        request.setPatientID(patientId);
        request.setAppointmentID(appointmentId);

        return reservationService.getReservationPort().makeReservation(request);
    }

    public static void main(String[] args) throws Exception {
        ReservationServiceClient client = new ReservationServiceClient();
    }
}
