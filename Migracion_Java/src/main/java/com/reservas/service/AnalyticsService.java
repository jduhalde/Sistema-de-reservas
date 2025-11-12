package com.reservas.service;

import com.reservas.dto.SyncRequest;
import com.reservas.model.Reserva;
import com.reservas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    private ReservaRepository reservaRepository;

    private final String PYTHON_API_URL = "http://analitica-app:8000/api/analytics/sync";

    public String sincronizarDatosConPython() {
        try {
            // Obtener todas las reservas
            List<Reserva> reservas = reservaRepository.findAll();

            if (reservas.isEmpty()) {
                return "No hay reservas para sincronizar";
            }

            // Crear el objeto de solicitud
            SyncRequest syncRequest = new SyncRequest(reservas);

            // Configurar RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SyncRequest> request = new HttpEntity<>(syncRequest, headers);

            // Enviar datos a Python
            ResponseEntity<String> response = restTemplate.postForEntity(
                    PYTHON_API_URL,
                    request,
                    String.class);

            System.out.println("✅ Sincronización exitosa con Python: " + response.getBody());
            return "Sincronización exitosa: " + reservas.size() + " reservas enviadas";

        } catch (Exception e) {
            System.err.println("❌ Error al sincronizar con Python: " + e.getMessage());
            return "Error en sincronización: " + e.getMessage();
        }
    }
}
