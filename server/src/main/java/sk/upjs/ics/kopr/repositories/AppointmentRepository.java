package sk.upjs.ics.kopr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.upjs.ics.kopr.entities.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


}