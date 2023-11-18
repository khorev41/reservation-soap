package sk.upjs.ics.kopr;

import jakarta.xml.ws.Endpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class Launcher {
    public static void main(String[] args) {
        List<Source> metadata = new ArrayList<>();

        var wsdlSource = new StreamSource(ReservationServiceImpl.class.getResourceAsStream("/reservation.wsdl"));
        wsdlSource.setSystemId("http://kopr.ics.upjs.sk/reservation.wsdl");
        metadata.add(wsdlSource);

        var xsdSource = new StreamSource(ReservationServiceImpl.class.getResourceAsStream("/reservation.xsd"));
        xsdSource.setSystemId("http://kopr.ics.upjs.sk/reservation.xsd");
        metadata.add(xsdSource);

        var filter = new HashMap<String, Object>();
        filter.put(Endpoint.WSDL_SERVICE, new QName("http://kopr.ics.upjs.sk", "ReservationService"));
        filter.put(Endpoint.WSDL_PORT, new QName("http://kopr.ics.upjs.sk", "ReservationPort"));

        var context = SpringApplication.run(Launcher.class, args);
        var appointmentsService = context.getBean(ReservationServiceImpl.class);
        var endpoint = Endpoint.create(appointmentsService);

        endpoint.setProperties(filter);
        endpoint.setMetadata(metadata);
        endpoint.publish("http://localhost:8888/reservation");
        System.out.println("Server running...");
    }
}