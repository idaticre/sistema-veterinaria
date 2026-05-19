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

    // 🔥 Ejecuta cada minuto
    @Scheduled(cron = "0 * * * * *")
    public void actualizarNoAsistidos() {

        LocalDate hoy = LocalDate.now();

        // 🔥 tolerancia de 30 minutos
        LocalTime horaActual =
                LocalTime.now().minusMinutes(30);

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

            System.out.println(
                    "✔ No hay citas para actualizar"
            );

            return;
        }

        for (AgendaEntity cita : citas) {

            EstadoAgendaEntity estado =
                    new EstadoAgendaEntity();

            // 🔥 ID del estado NO ASISTIÓ
            estado.setId(6);

            cita.setEstado(estado);
        }

        agendaRepository.saveAll(citas);

        System.out.println(
                "✔ Citas actualizadas a NO ASISTIÓ"
        );
    }
}