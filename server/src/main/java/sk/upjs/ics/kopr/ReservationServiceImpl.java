package sk.upjs.ics.kopr;

import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;
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
    public ReservationResponse makeReservation(ReservationRequest reservationRequest) {
        if(reservationRequest.getPatientID().length() > 11 || reservationRequest.getPatientID().isBlank()){
            return new ReservationResponse();
        }

        Optional<Appointment> optionalAppointment = appointmentService.getAppointmentById(reservationRequest.getAppointmentID());

        if (optionalAppointment.isPresent()){
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
        }else {
            return new ReservationResponse();
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
