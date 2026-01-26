package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.EloCalculationRequest;
import org.example.be_eproject_sem4.Dto.EloCalculationResponse;
import org.example.be_eproject_sem4.Service.EloService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/elo")
public class EloController {

    private final EloService eloService;

    public EloController(EloService eloService) {
        this.eloService = eloService;
    }

    @PostMapping("/calculate")
    public EloCalculationResponse calculate(@RequestBody EloCalculationRequest request) {
        return eloService.calculateNewElo(request);
    }
}
