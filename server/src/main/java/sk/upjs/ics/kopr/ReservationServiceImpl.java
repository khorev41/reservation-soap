package sk.upjs.ics.kopr;

import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.upjs.ics.kopr.entities.Appointment;
import sk.upjs.ics.kopr.services.AppointmentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@WebService(endpointInterface = "sk.upjs.ics.kopr.ReservationPortType")
public class ReservationServiceImpl implements ReservationPortType {

    @Autowired
    private AppointmentService appointmentService;

    @Override
    public ReservationResponse makeReservation(ReservationRequest reservationRequest) throws ReservationFaultMessage {
        validateReservationRequest(reservationRequest);

        Optional<Appointment> optionalAppointment = appointmentService.getAppointmentById(reservationRequest.getAppointmentID());


        if (optionalAppointment.isEmpty()) {
            ReservationFault reservationFault = new ReservationFault();
            reservationFault.setErrorMessage("Error: Appointment did not exist");
            throw new ReservationFaultMessage("Error: Invalid Reservation Request", reservationFault);
        }

        if (optionalAppointment.get().getPatientId() != null) {
            ReservationFault reservationFault = new ReservationFault();
            reservationFault.setErrorMessage("Error: Appointment is not free");
            throw new ReservationFaultMessage("Error: Invalid Reservation Request", reservationFault);
        }


        Appointment appointment = optionalAppointment.get();
        appointment.setPatientId(reservationRequest.getPatientID());

        appointmentService.saveAppointment(appointment);

        ReservationResponse reservationResponse = new ReservationResponse();
        reservationResponse.setAppointmentID(appointment.getId());
        reservationResponse.setPatientID(appointment.getPatientId());
        reservationResponse.setAppointmentDate(formatDate(appointment.getDatetime()));
        reservationResponse.setAppointmentTime(formatTime(appointment.getDatetime()));
        reservationResponse.setDoctorName(appointment.getDoctor().getName());
        return reservationResponse;

    }

    private void validateReservationRequest(ReservationRequest reservationRequest) throws ReservationFaultMessage {
        ReservationFault reservationFault = new ReservationFault();
        String patientID = reservationRequest.getPatientID();

        if (patientID == null) {
            reservationFault.setErrorMessage("Error: Patient ID is null.");
            throw new ReservationFaultMessage("Error: Invalid Reservation Request", reservationFault);
        }

        if (patientID.length() > 11) {
            reservationFault.setErrorMessage("Error: Patient ID length exceeds maximum limit (11 characters).");
            throw new ReservationFaultMessage("Error: Invalid Reservation Request", reservationFault);
        }

        if (patientID.isBlank()) {
            reservationFault.setErrorMessage("Error: Patient ID is blank.");
            throw new ReservationFaultMessage("Error: Invalid Reservation Request", reservationFault);
        }

        int appointmentID = reservationRequest.getAppointmentID();
        if (appointmentID == 0) {
            reservationFault.setErrorMessage("Error: Appointment ID is 0.");
            throw new ReservationFaultMessage("Error: Invalid Reservation Request", reservationFault);
        }
    }

    private static String formatDate(LocalDateTime localDateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(dateFormatter);
    }

    private static String formatTime(LocalDateTime localDateTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localDateTime.format(timeFormatter);
    }
}
