package Restful_Web_Service.server;

import Restful_Web_Service.model.Home;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class HomeController {

    @GetMapping("/")
    public EntityModel<Home> home() {
        EntityModel<Home> response = EntityModel.of(new Home());

        response.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        response.add(linkTo(methodOn(CarController.class).getAllCars()).withRel("cars"));
        response.add(linkTo(methodOn(CarController.class).getAllCars()).withRel("cars-v2"));
    
    
        return response;
    }
}
