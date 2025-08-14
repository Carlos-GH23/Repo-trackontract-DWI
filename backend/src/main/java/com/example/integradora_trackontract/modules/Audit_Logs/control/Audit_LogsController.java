package com.example.integradora_trackontract.modules.Audit_Logs.control;

import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_Logs;
import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class Audit_LogsController {

    private final Audit_LogsRepository repo;

    @GetMapping("/latest")
    public ResponseEntity<List<Audit_Logs>> latest() {
        return ResponseEntity.ok(repo.findAll()
                .stream().sorted((a,b)->b.getId().compareTo(a.getId()))
                .limit(50).toList());
    }
}