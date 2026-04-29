package com.vet.manadawoof.scheduler;

import com.vet.manadawoof.entity.AgendaEntity;
import com.vet.manadawoof.entity.EstadoAgendaEntity;
import com.vet.manadawoof.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AgendaScheduler {

    private final AgendaRepository agendaRepository;

     @Scheduled(cron = "0 0 0 * * *") // todos los días a medianoche
    public void actualizarNoAsistidos() {

        LocalDate hoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        // 🔥 estados válidos
        List<Integer> estadosValidos = List.of(
            1, // PENDIENTE
            2, // CONFIRMADA
            3  // REPROGRAMADA
        );

        List<AgendaEntity> citas =
                agendaRepository.findCitasParaNoAsistido(
                        hoy,
                        horaActual,
                        estadosValidos
                );

        if (citas.isEmpty()) {
            System.out.println("✔ No hay citas para actualizar");
            return;
        }

        for (AgendaEntity cita : citas) {
            EstadoAgendaEntity estado = new EstadoAgendaEntity();
            estado.setId(6); // 👈 NO ASISTIO (verifica ID real)

            cita.setEstado(estado);
        }

        agendaRepository.saveAll(citas);

        System.out.println("✔ Citas actualizadas a NO ASISTIO");
    }
}