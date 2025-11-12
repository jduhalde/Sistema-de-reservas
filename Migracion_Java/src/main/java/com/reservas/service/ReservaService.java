package com.reservas.service;

import com.reservas.model.Reserva;
import com.reservas.model.Persona;
import com.reservas.model.Articulo;
import com.reservas.model.Sala;
import com.reservas.repository.ReservaRepository;
import com.reservas.repository.PersonaRepository;
import com.reservas.repository.ArticuloRepository;
import com.reservas.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final ArticuloRepository articuloRepository;
    private final SalaRepository salaRepository;

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        // Validar que la persona existe
        if (reserva.getPersona() == null || reserva.getPersona().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe especificar una persona para la reserva");
        }

        Persona persona = personaRepository.findById(reserva.getPersona().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Persona no encontrada con ID: " + reserva.getPersona().getId()));

        reserva.setPersona(persona);

        // Validar fechas
        if (reserva.getFechaHoraInicio() == null || reserva.getFechaHoraFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe especificar fecha y hora de inicio y fin");
        }

        if (reserva.getFechaHoraInicio().isAfter(reserva.getFechaHoraFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (reserva.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se pueden crear reservas en el pasado");
        }

        // 🆕 VALIDACIÓN DE CONFLICTOS PARA SALA
        if (reserva.getSala() != null && reserva.getSala().getId() != null) {
            Sala sala = salaRepository.findById(reserva.getSala().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Sala no encontrada con ID: " + reserva.getSala().getId()));

            // Buscar conflictos con otras reservas de la misma sala
            List<Reserva> conflictosSala = reservaRepository.findConflictingSalaReservations(
                    sala.getId(),
                    reserva.getFechaHoraInicio(),
                    reserva.getFechaHoraFin());

            if (!conflictosSala.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "La sala ya está reservada en ese horario");
            }

            reserva.setSala(sala);
        }

        // 🆕 VALIDACIÓN DE CONFLICTOS PARA ARTÍCULO
        if (reserva.getArticulo() != null && reserva.getArticulo().getId() != null) {
            Articulo articulo = articuloRepository.findById(reserva.getArticulo().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Artículo no encontrado con ID: " + reserva.getArticulo().getId()));

            // Buscar conflictos con otras reservas del mismo artículo
            List<Reserva> conflictosArticulo = reservaRepository.findConflictingArticuloReservations(
                    articulo.getId(),
                    reserva.getFechaHoraInicio(),
                    reserva.getFechaHoraFin());

            if (!conflictosArticulo.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El artículo ya está reservado en ese horario");
            }

            reserva.setArticulo(articulo);
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reserva no encontrada con ID: " + id));

        // Validar fechas si se actualizan
        if (reservaActualizada.getFechaHoraInicio() != null && reservaActualizada.getFechaHoraFin() != null) {
            if (reservaActualizada.getFechaHoraInicio().isAfter(reservaActualizada.getFechaHoraFin())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La fecha de inicio debe ser anterior a la fecha de fin");
            }

            // 🆕 VALIDACIÓN DE CONFLICTOS AL ACTUALIZAR SALA (excluyendo la propia reserva)
            if (reservaActualizada.getSala() != null && reservaActualizada.getSala().getId() != null) {
                Long salaId = reservaActualizada.getSala().getId();

                List<Reserva> conflictosSala = reservaRepository.findConflictingSalaReservationsExcluding(
                        salaId,
                        id,
                        reservaActualizada.getFechaHoraInicio(),
                        reservaActualizada.getFechaHoraFin());

                if (!conflictosSala.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "La sala ya está reservada en ese horario");
                }

                Sala sala = salaRepository.findById(salaId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Sala no encontrada con ID: " + salaId));
                reserva.setSala(sala);
            }

            // 🆕 VALIDACIÓN DE CONFLICTOS AL ACTUALIZAR ARTÍCULO (excluyendo la propia
            // reserva)
            if (reservaActualizada.getArticulo() != null && reservaActualizada.getArticulo().getId() != null) {
                Long articuloId = reservaActualizada.getArticulo().getId();

                List<Reserva> conflictosArticulo = reservaRepository.findConflictingArticuloReservationsExcluding(
                        articuloId,
                        id,
                        reservaActualizada.getFechaHoraInicio(),
                        reservaActualizada.getFechaHoraFin());

                if (!conflictosArticulo.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "El artículo ya está reservado en ese horario");
                }

                Articulo articulo = articuloRepository.findById(articuloId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Artículo no encontrado con ID: " + articuloId));
                reserva.setArticulo(articulo);
            }

            reserva.setFechaHoraInicio(reservaActualizada.getFechaHoraInicio());
            reserva.setFechaHoraFin(reservaActualizada.getFechaHoraFin());
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reserva no encontrada con ID: " + id));
        reservaRepository.delete(reserva);
    }

    public List<Reserva> obtenerReservasPorPersona(Long personaId) {
        return reservaRepository.findAll().stream()
                .filter(r -> r.getPersona() != null && r.getPersona().getId().equals(personaId))
                .toList();
    }

    public List<Reserva> obtenerReservasPorSala(Long salaId) {
        return reservaRepository.findAll().stream()
                .filter(r -> r.getSala() != null && r.getSala().getId().equals(salaId))
                .toList();
    }
}