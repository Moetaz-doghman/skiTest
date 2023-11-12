package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import tn.esprit.spring.entities.Course;
import tn.esprit.spring.services.RegistrationServicesImpl;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Skier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
class RegistrationServicesImplTest {

    private RegistrationServicesImpl registrationServices;

    private ISkierRepository skierRepository;
    private ICourseRepository courseRepository;
    private IInstructorRepository instructorRepository;
    private IRegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() {
        skierRepository = mock(ISkierRepository.class);
        courseRepository = mock(ICourseRepository.class);
        instructorRepository = mock(IInstructorRepository.class);
        registrationRepository = mock(IRegistrationRepository.class);

        registrationServices = new RegistrationServicesImpl(
                registrationRepository, skierRepository, courseRepository, instructorRepository
        );
    }

    @Test
    void addRegistrationAndAssignToSkier() {
        // Arrange
        Registration registration = new Registration();
        Skier skier = new Skier();
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(skier));
        when(registrationRepository.save(registration)).thenReturn(registration);

        // Act
        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        // Assert
        verify(skierRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(registration);
        assertEquals(result, registration);
    }

    @Test
    void assignRegistrationToCourse() {
        // Arrange
        Registration registration = new Registration();
        when(registrationRepository.findById(anyLong())).thenReturn(Optional.of(registration));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(new Course()));
        when(registrationRepository.save(registration)).thenReturn(registration);

        // Act
        Registration result = registrationServices.assignRegistrationToCourse(1L, 2L);

        // Assert
        verify(registrationRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(2L);
        verify(registrationRepository, times(1)).save(registration);
        assertEquals(result, registration);
    }

    // Add more test cases for other methods as needed
}
