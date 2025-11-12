package com.reservas.repository;

import com.reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // 🆕 NUEVO: Busca conflictos de reservas para una SALA
    /**
     * Busca reservas que se superponen en el tiempo para una sala específica.
     * Superposición: (Inicio1 < Fin2) AND (Fin1 > Inicio2)
     */
    @Query("SELECT r FROM Reserva r " +
            "WHERE r.sala.id = :salaId " +
            "AND r.fechaHoraInicio < :fechaFin " +
            "AND r.fechaHoraFin > :fechaInicio")
    List<Reserva> findConflictingSalaReservations(
            @Param("salaId") Long salaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // 🆕 NUEVO: Busca conflictos de reservas para un ARTÍCULO
    /**
     * Busca reservas que se superponen en el tiempo para un artículo específico.
     * Superposición: (Inicio1 < Fin2) AND (Fin1 > Inicio2)
     */
    @Query("SELECT r FROM Reserva r " +
            "WHERE r.articulo.id = :articuloId " +
            "AND r.fechaHoraInicio < :fechaFin " +
            "AND r.fechaHoraFin > :fechaInicio")
    List<Reserva> findConflictingArticuloReservations(
            @Param("articuloId") Long articuloId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // 🆕 NUEVO: Busca conflictos excluyendo la propia reserva (para actualización)
    @Query("SELECT r FROM Reserva r " +
            "WHERE r.sala.id = :salaId " +
            "AND r.id != :reservaId " +
            "AND r.fechaHoraInicio < :fechaFin " +
            "AND r.fechaHoraFin > :fechaInicio")
    List<Reserva> findConflictingSalaReservationsExcluding(
            @Param("salaId") Long salaId,
            @Param("reservaId") Long reservaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT r FROM Reserva r " +
            "WHERE r.articulo.id = :articuloId " +
            "AND r.id != :reservaId " +
            "AND r.fechaHoraInicio < :fechaFin " +
            "AND r.fechaHoraFin > :fechaInicio")
    List<Reserva> findConflictingArticuloReservationsExcluding(
            @Param("articuloId") Long articuloId,
            @Param("reservaId") Long reservaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}