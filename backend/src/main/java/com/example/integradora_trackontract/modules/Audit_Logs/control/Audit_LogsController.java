package com.example.integradora_trackontract.modules.Audit_Logs.control;

import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_Logs;
import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_LogsDTO;
import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class Audit_LogsController {

    private final Audit_LogsRepository repo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, h:mm:ss a");

    @GetMapping("/latest")
    public ResponseEntity<List<Audit_LogsDTO>> latest() {
        List<Audit_LogsDTO> logs = repo.findAll()
                .stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(50)
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(logs);
    }

    private Audit_LogsDTO toDTO(Audit_Logs log) {
        String usuario = log.getUser_id() != null
                ? log.getUser_id().getName() + " " + (log.getUser_id().getLastName() != null ? log.getUser_id().getLastName() : "")
                : "Anónimo";

        String rol = (log.getUser_id() != null && log.getUser_id().getRol_id() != null)
                ? log.getUser_id().getRol_id().getName()
                : "Sin rol";

        String fecha = log.getCreated_at() != null
                ? log.getCreated_at().format(formatter)
                : "";

        return new Audit_LogsDTO(
                log.getId(),
                usuario,
                rol,
                log.getMethod(),
                log.getPath(),
                fecha
        );
    }
}