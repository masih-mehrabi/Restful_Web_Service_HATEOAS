package Restful_Web_Service.server;

import Restful_Web_Service.model.Rental;
import Restful_Web_Service.model.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.time.LocalDateTime.now;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    
    @GetMapping("/users/{userId}/rentals")
    public List<Rental> getAllActiveRentalsByUser(@PathVariable("userId") String userId) {
    
    
        List<Rental> singleUserRentals = new ArrayList<>();
        User user = ModelStorage.getUserById(userId);
    
        if (hasActiveRental(user)) {
            for (Rental rental: ModelStorage.getAllRentals()) {
                if (Objects.equals(rental.getUser().getId(), user.getId()) && isActive(rental)) {
                    singleUserRentals.add(rental);
                }
            }
        }
        return singleUserRentals;
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllUsers() {
        List<User> result = ModelStorage.getAllUsers();

        List<EntityModel<User>> resultWithLinks = result.stream().map(user -> {
            EntityModel<User> entityModel = EntityModel.of(user);

            entityModel.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
            entityModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));

            if (hasActiveRental(user)) {
                entityModel.add(linkTo(methodOn(UserController.class).getAllActiveRentalsByUser(user.getId())).withRel("rentals"));
    
            }
            return entityModel;
        }).toList();

        return CollectionModel.of(resultWithLinks, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

   @GetMapping("/users/{id}")
    public EntityModel<User> getUserById(@PathVariable String id) {
        User user = ModelStorage.getUserById(id);

        if (user == null) {
            throw new UserNotFoundException();
        }

        EntityModel<User> resultWithLinks = EntityModel.of(user);

        resultWithLinks.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        resultWithLinks.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        
        if (hasActiveRental(user)) {
            resultWithLinks.add(linkTo(methodOn(UserController.class).getAllActiveRentalsByUser(user.getId())).withRel("rentals"));
    
        }
        
        return resultWithLinks;
    }

    private boolean hasActiveRental(User user) {
        boolean output = false;
        for (Rental rental: ModelStorage.getAllRentals()) {
            if (isActive(rental)
                        && Objects.equals(rental.getUser().getId(), user.getId())) {
                output = true;
            }
        }
        return output;
    }

    private boolean isActive(Rental rental) {
        return rental.getStartDate().isBefore(now()) && rental.getEndDate().isAfter(now());
    }
}
