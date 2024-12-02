package com.oliveiradev.ReceptorGPS.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oliveiradev.ReceptorGPS.Controller.dto.CreatePointOfInterest;
import com.oliveiradev.ReceptorGPS.Entity.PointOfInterest;
import com.oliveiradev.ReceptorGPS.Repository.PointOfInterestRepository;

@RestController 
public class PointOfInterestController {
    private final PointOfInterestRepository repository;
    
    public PointOfInterestController(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/Point-of-interest")
    public ResponseEntity<Void> createPointInterest(@RequestBody CreatePointOfInterest body) {
        repository.save(new PointOfInterest(body.name(), body.x(), body.y()));
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/Point-of-interest")
    public ResponseEntity<Page<PointOfInterest>> ListPointInterest(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        var body = repository.findAll(PageRequest.of(page, pageSize));
        
        return ResponseEntity.ok(body);
    }

    @GetMapping("/near-me")
    public ResponseEntity<List<PointOfInterest>> nearMe (@RequestParam("x") Long x,
                                                        @RequestParam("y") Long y,
                                                        @RequestParam("dmax") Long dmax) {
        var xMin = x - dmax;
        var xMax = x - dmax;
        var yMin = y - dmax;
        var yMax = y - dmax;

        var body = repository.findNearMe(xMin, xMax, yMin, yMax)
        .stream()
        .filter(p -> distanceBetweenPoints(x, y, p.getX(), p.getY()) <= dmax)
        .toList();
        
        return ResponseEntity.ok(body);
    }

    private Double distanceBetweenPoints(Long x1, Long y1, Long x2, Long y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
