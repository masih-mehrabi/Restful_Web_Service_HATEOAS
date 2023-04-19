package Restful_Web_Service.server;

import Restful_Web_Service.model.Car;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class CarController {
    @GetMapping("/cars")
    public List<Car> getAllCars() {
        
        return ModelStorage.getAllCars();
    }

    @GetMapping("/cars/{id}")
    public Car getCarById(@PathVariable String id) {
       if (ModelStorage.getCarById(id) == null) {
           throw new CarNotFoundException();
       } else {
           return ModelStorage.getCarById(id);
       }
    }

    @PostMapping("/cars")
    public Car createCar(@RequestBody Car newCar) {
        newCar.setId(ModelStorage.createRandomId());
        ModelStorage.getAllCars().add(newCar);
        return newCar;
    }

    @PutMapping("/cars")
    public Car updateCar(@RequestBody Car updatedCar) {
        ModelStorage.saveCar(updatedCar);
        return updatedCar;
    
    }

    @DeleteMapping("/cars/{id}")
    public Car deleteCar(@PathVariable String id) {
        if (ModelStorage.getCarById(id) == null) {
            throw new CarNotFoundException();
        } else {
            ModelStorage.deleteCar(ModelStorage.getCarById(id));
            return ModelStorage.getCarById(id);
        }
    }
    
    @GetMapping("/cars-v2")
    public CollectionModel<EntityModel<Car>> getAllCarsV2() {
    
    
        List<Car> cars = ModelStorage.getAllCars();

        
        List<EntityModel<Car>> resultWithLinks = cars.stream().map(car -> {
            EntityModel<Car> entityModel = EntityModel.of(car);
            entityModel.add(linkTo(methodOn(CarController.class).getCarById(car.getId())).withSelfRel());
            entityModel.add(linkTo(methodOn(CarController.class).getAllCars()).withRel("cars"));
            
            return entityModel;
        }).toList();
    
    
        return CollectionModel.of(resultWithLinks, linkTo(methodOn(CarController.class).getAllCarsV2()).withSelfRel());
    }
}
